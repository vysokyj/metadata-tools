package cz.jerzy.id3norm;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.text.*;

/**
 * @auhtor Jiří Vysoký
 */
public class Frame {

    private MP3File file;
    private ID3v24Tag tag;
    private ID3v24Frame frame;

    private final StringProperty pathProperty;
    private final StringProperty idProperty;
    private final StringProperty version;
    private final StringProperty encoding;
    private final StringProperty normalisation;
    private final StringProperty value;

    public Frame(MP3File file, ID3v24Tag tag, ID3v24Frame frame) {
        this.file = file;
        this.tag = tag;
        this.frame = frame;

        this.pathProperty = new SimpleStringProperty(file.getFile().getName());
        this.idProperty = new SimpleStringProperty(frame.getId());
        this.version = new SimpleStringProperty(file.getID3v2Tag().getIdentifier());
        this.encoding = new SimpleStringProperty(frame.getEncoding());
        this.normalisation = new SimpleStringProperty(getNormalisation(frame.getContent()));
        this.value = new SimpleStringProperty(frame.getContent());
    }

    public StringProperty pathProperty() {
        return pathProperty;
    }

    public StringProperty idProperty() {
        return idProperty;
    }

    public StringProperty versionProperty() {
        return version;
    }

    public StringProperty encodingProperty() {
        return encoding;
    }

    public StringProperty normalisationProperty() {
        return normalisation;
    }

    public StringProperty valueProperty() {
        return value;
    }

    private static String getNormalisation(String s) {
        if (Normalizer.isNormalized(s, Normalizer.Form.NFC)) return "NFC";
        if (Normalizer.isNormalized(s, Normalizer.Form.NFD)) return "NFD";
        if (Normalizer.isNormalized(s, Normalizer.Form.NFKC)) return "NFKC";
        if (Normalizer.isNormalized(s, Normalizer.Form.NFKD)) return "NFKD";
        return "???";
    }

}