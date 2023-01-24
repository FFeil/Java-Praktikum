package de.feil.controller.database;

import de.feil.controller.references.ReferenceHandler;
import de.feil.view.dialog.AlertHelper;
import de.feil.view.dialog.ChooseSettingDialog;
import de.feil.view.dialog.SaveSettingsDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class DatabaseController{

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_NAME = "SettingsDB";
    private static final String DB_URL_PREFIX = "jdbc:derby:" + DB_NAME;
    private static final String DB_URL = DB_URL_PREFIX + ";create=false";
    private static final String DB_URL_CREATE = DB_URL_PREFIX + ";create=true";

    private static final String TABLENAME = "SETTING";
    private static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLENAME + " (" +
            "name VARCHAR(40) PRIMARY KEY, " +
            "xPosition DOUBLE NOT NULL, yPosition DOUBLE NOT NULL, " +
            "windowHeight DOUBLE NOT NULL, windowWidth DOUBLE NOT NULL, " +
            "cellHeight INTEGER NOT NULL, cellWidth INTEGER NOT NULL, " +
            "sliderSpeed DOUBLE NOT NULL)";
    private static final String INSERT_STATEMENT = "INSERT INTO " + TABLENAME + " values (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_STATEMENT =
            "UPDATE " + TABLENAME + " SET " +
                    "xPosition = ?, yPosition = ?, " +
                    "windowHeight = ?, windowWidth = ?, " +
                    "cellHeight = ?, cellWidth = ?, " +
                    "sliderSpeed = ? " +
                    "WHERE name = ?";
    private static final String SELECT_ROW_STATEMENT = "SELECT * FROM " + TABLENAME + " WHERE name = ?";
    private static final String SELECT_NAMES_STATEMENT = "SELECT name FROM " + TABLENAME;
    private static final String COUNT_STATEMENT = "SELECT COUNT(*) FROM " + TABLENAME + " WHERE name = ?";
    private static final String DELETE_STATEMENT = "DELETE FROM " + TABLENAME + " WHERE name = ?";
    private Connection connection = null;

    private final ReferenceHandler referenceHandler;

    public DatabaseController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;

        referenceHandler.setDatabaseController(this);
        if (init()) {
            referenceHandler.getMainController().getSaveSettingsMenuItem().setOnAction(e -> saveSettings());
            referenceHandler.getMainController().getRestoreSettingsMenuItem().setOnAction(e -> restoreSettings());
            referenceHandler.getMainController().getDeleteSettingsMenuItem().setOnAction(e -> deleteSettings());

            updateMenuItems();
        }
    }

    private boolean init() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            AlertHelper.showError(referenceHandler.getName(), "Beim Laden der Datenbank ist etwas schief gelaufen:\n" + e);

            return false;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL_CREATE);
            ResultSet resultSet = connection.getMetaData().getTables(null, null, TABLENAME, null)) {
            if (!resultSet.next()) {
                createTable(connection);
            }
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Beim Laden der Datenbank ist etwas schief gelaufen:\n" + e);

            return false;
        }

        return true;
    }

    private void createTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_STATEMENT);
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Beim Laden der Datenbank ist etwas schief gelaufen:" + e);

            throw e;
        }
    }

    private Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(0)) {
                return connection;
            }
            if (connection != null) {
                connection.close();
                return getConnection();
            }
            connection = DriverManager.getConnection(DB_URL);
            return connection;
        } catch (SQLException exc) {
            return null;
        }
    }

    public void shutdown() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                AlertHelper.showError("Beim schließen der Datenbank ist etwas schiefgelaufen:\n" + e);
            }
        }
        if (DRIVER.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {

            }
        }
    }

    private void saveSettings() {
        try {
            Connection conn = getConnection();
            if (conn == null) {
                AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler ist beim Schreiben aufgetreten!");

                return;
            }

            Optional<String> name = new SaveSettingsDialog().showAndWait();
            if (name.isEmpty()) {
                return;
            }

            if (rowExists(name.get())) {
                updateRow(name.get());
            } else {
                insertRow(name.get());

                if (!selectAllNames().isEmpty()) {
                    for (ReferenceHandler refHandler: referenceHandler.getReferenceHandlers()) {
                        refHandler.getDatabaseController().updateMenuItems();
                    }
                }
            }
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler ist beim Schreiben aufgetreten!");
        }
    }

    private void restoreSettings() {
        Connection conn = getConnection();
        if (conn == null) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Lesen ist aufgetreten!");

            return;
        }

        ArrayList<String> settings = selectAllNames();
        if (settings.isEmpty()) {
            return;
        }

        Optional<String> settingToRestore = new ChooseSettingDialog(settings,
                "Einstellung wiederherstellen").showAndWait();
        if (settingToRestore.isEmpty()) {
            return;
        }

        try (PreparedStatement selectRowStmt = connection.prepareStatement(SELECT_ROW_STATEMENT)) {
            selectRowStmt.setString(1, settingToRestore.get());
            ResultSet resultSet = selectRowStmt.executeQuery();

            if (resultSet.next()) {
                referenceHandler.getMainStage().setX(resultSet.getDouble("xPosition"));
                referenceHandler.getMainStage().setY(resultSet.getDouble("yPosition"));
                referenceHandler.getMainStage().setHeight(resultSet.getDouble("windowHeight"));
                referenceHandler.getMainStage().setWidth(resultSet.getDouble("windowWidth"));
                referenceHandler.getPopulationPanel().setCellSize(
                        resultSet.getInt("cellHeight"), resultSet.getInt("cellWidth"));
                referenceHandler.getSimulationController().setSpeed(resultSet.getInt("sliderSpeed"));

            }
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Lesen ist aufgetreten!");
        }
    }

    private void deleteSettings() {
        try {
            Connection conn = getConnection();
            if (conn == null) {
                AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Löschen ist aufgetreten!");

                return;
            }

            deleteRow();

            if (selectAllNames().isEmpty()) {
                for (ReferenceHandler refHandler: referenceHandler.getReferenceHandlers()) {
                    refHandler.getDatabaseController().updateMenuItems();
                }
            }
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Löschen ist aufgetreten!");
        }
    }


    private boolean rowExists(String name) {
        try (PreparedStatement countStmt = connection.prepareStatement(COUNT_STATEMENT)) {
            countStmt.setString(1, name);
            ResultSet resultSet = countStmt.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            AlertHelper.showError("Ein Datenbankfehler ist aufgetreten:\n" + e);
        }

        return false;
    }

    private void insertRow(String name) throws SQLException {
        try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_STATEMENT)) {
            connection.setAutoCommit(false);

            insertStmt.setString(1, name);
            insertStmt.setDouble(2, referenceHandler.getMainStage().getX());
            insertStmt.setDouble(3, referenceHandler.getMainStage().getY());
            insertStmt.setDouble(4, referenceHandler.getMainStage().getHeight());
            insertStmt.setDouble(5, referenceHandler.getMainStage().getWidth());
            insertStmt.setInt(6, referenceHandler.getPopulationPanel().getCellWidth());
            insertStmt.setInt(7, referenceHandler.getPopulationPanel().getCellHeight());
            insertStmt.setDouble(8, referenceHandler.getMainController().getSlider().getValue());

            insertStmt.execute();
            connection.commit();
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler ist beim Scheiben aufgetreten:\n" + e);
            connection.rollback();

            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void updateRow(String name) throws SQLException {
        try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_STATEMENT)) {
            connection.setAutoCommit(false);

            updateStmt.setDouble(1, referenceHandler.getMainStage().getX());
            updateStmt.setDouble(2, referenceHandler.getMainStage().getY());
            updateStmt.setDouble(3, referenceHandler.getMainStage().getHeight());
            updateStmt.setDouble(4, referenceHandler.getMainStage().getWidth());
            updateStmt.setInt(5, referenceHandler.getPopulationPanel().getCellWidth());
            updateStmt.setInt(6, referenceHandler.getPopulationPanel().getCellHeight());
            updateStmt.setDouble(7, referenceHandler.getMainController().getSlider().getValue());
            updateStmt.setString(8, name);

            updateStmt.execute();
            connection.commit();
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler ist beim Scheiben aufgetreten:\n" + e);
            connection.rollback();

            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private ArrayList<String> selectAllNames() {
        Connection conn = getConnection();
        if (conn == null) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Löschen ist aufgetreten!");

            return new ArrayList<>();
        }

        ArrayList<String> result = new ArrayList<>();

        try (Statement selectStmt = connection.createStatement();
             ResultSet resultSet = selectStmt.executeQuery(SELECT_NAMES_STATEMENT)) {

            while (resultSet.next()) {
                result.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler ist beim Lesen aufgetreten:\n" + e);
        }

        return result;
    }

    private void deleteRow() throws SQLException {
        ArrayList<String> settings = selectAllNames();
        if (settings.isEmpty()) {
            return;
        }

        Optional<String> settingToDelete = new ChooseSettingDialog(settings, "Einstellung löschen").showAndWait();
        if (settingToDelete.isEmpty()) {
            return;
        }

        try (PreparedStatement deleteStmt = connection.prepareStatement(DELETE_STATEMENT)) {
            connection.setAutoCommit(false);

            deleteStmt.setString(1, settingToDelete.get());
            deleteStmt.execute();
            connection.commit();
        } catch (SQLException e) {
            AlertHelper.showError(referenceHandler.getName(), "Ein Datenbankfehler beim Löschen ist aufgetreten!");
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void updateMenuItems() {
        if (selectAllNames().isEmpty()) {
            referenceHandler.getMainController().getRestoreSettingsMenuItem().setDisable(true);
            referenceHandler.getMainController().getDeleteSettingsMenuItem().setDisable(true);
        } else {
            referenceHandler.getMainController().getRestoreSettingsMenuItem().setDisable(false);
            referenceHandler.getMainController().getDeleteSettingsMenuItem().setDisable(false);
        }
    }
}
