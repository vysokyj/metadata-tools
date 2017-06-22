package cz.jerzy.id3norm;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.File;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Jiri Vysoky
 */
public class MainController implements Initializable {

    private ObservableList<Frame> frameList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private ObservableList<File> fileList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());


    @FXML
    private MenuBar menuBar;

    @FXML
    private TableView<Frame> tableView;

    @FXML
    private TableColumn<Frame, String> pathTableColumn;

    @FXML
    private TableColumn<Frame, String> idTableColumn;

    @FXML
    private TableColumn<Frame, String> versionTableColumn;

    @FXML
    private TableColumn<Frame, String> encodingTableColumn;

    @FXML
    private TableColumn<Frame, String> normalisationTableColumn;

    @FXML
    private TableColumn<Frame, String> valueTableColumn;


    @FXML
    private ProgressBar progressBar;

    @FXML
    private void handleOpenAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(menuBar.getScene().getWindow());
        if (selectedDirectory != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            fileList.clear();
            frameList.clear();
            progressBar.setProgress(0d);
            progressBar.setMaxHeight(1d);
            executor.submit(() -> {
                findFiles(selectedDirectory);
                int size = fileList.size();
                int item = 0;
                for (File file : fileList) {
                    setProgress(((double) ++item) / ((double) size));
                    loadTags(file);
                }
                executor.shutdown(); // this thread runs forever
            });
        }
    }

    @FXML
    private void handleCloseAction() {
        Platform.exit();
        System.exit(0);
    }


    @SuppressWarnings("unchecked")
    public void initialize(URL url, ResourceBundle rb) {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
            menuBar.setMaxHeight(0d);
        }
        tableView.setItems(frameList);
        pathTableColumn.setCellValueFactory(new PropertyValueFactory("path"));
        idTableColumn.setCellValueFactory(new PropertyValueFactory("id"));
        versionTableColumn.setCellValueFactory(new PropertyValueFactory("version"));
        encodingTableColumn.setCellValueFactory(new PropertyValueFactory("encoding"));
        normalisationTableColumn.setCellValueFactory(new PropertyValueFactory("normalisation"));
        valueTableColumn.setCellValueFactory(new PropertyValueFactory("value"));
    }

    private void findFiles(File file) {
        if (file.isFile()) {
            String name = file.getName();
            String ext = name.substring(name.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("mp3")) fileList.add(file);
        } else if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(this::findFiles);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTags(File file) {
        //System.out.println(file.getAbsolutePath());

        try {
            final MP3File f = (MP3File) AudioFileIO.read(file);
            if (!f.hasID3v2Tag()) return;
            final ID3v24Tag t = f.getID3v2TagAsv24();
            t.frameMap.forEach((k, v) -> {
                if (v instanceof ID3v24Frame) frameList.add(new Frame(f, t, (ID3v24Frame) v));
                else if (v instanceof ArrayList) {
                    ArrayList l = (ArrayList) v;
                    l.forEach(v2 -> frameList.add(new Frame(f, t, (ID3v24Frame) v2)));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProgress(double progress) {
        //System.out.println(progress);
        progressBar.setProgress(progress);
    }
}
