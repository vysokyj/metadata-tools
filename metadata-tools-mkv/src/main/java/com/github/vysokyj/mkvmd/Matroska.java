package com.github.vysokyj.mkvmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Matroska {

    private File file;
    private File largeCover;
    private File smallCover;
    private String title = null;

    private boolean doTitle = false;
    private boolean doCover = false;
    /** Name of large version of cover art, when picture is portrait or square. */
    public static final String LARGE_COVER_PORT_NAME = "cover.jpg";
    /** Name of small version of cover art, when picture is portrait or square. */
    public static final String SMALL_COVER_PORT_NAME = "small_cover.png";
    /** Name of large version of cover art, when picture is landscape. */
    public static final String LARGE_COVER_LAND_NAME = "cover_land.jpg";
    /** Name of small version of cover art, when picture is landscape. */
    public static final String SMALL_COVER_LAND_NAME = "small_cover_land.jpg";
    public static final int LARGE_COVER_PORT_WIDTH = 600;
    public static final int SMALL_COVER_PORT_WIDTH = 120;
    public static final int LARGE_COVER_LAND_HEIGHT = LARGE_COVER_PORT_WIDTH;
    public static final int SMALL_COVER_LAND_HEIGHT = SMALL_COVER_PORT_WIDTH;

    public Matroska(File file) {
        this.file = file;
    }

    void setCoverFiles(File largeCover, File smallCover) {
        this.largeCover = largeCover;
        this.smallCover = smallCover;
        this.doCover = true;
    }

    public void setTitle(String title) {
        this.title = title;
        this.doTitle = title != null && title.length() > 0;
    }

    private void deleteAttachmentWithName(String name) {
        List<String> args = new ArrayList<>();
        args.add("mkvpropedit");
        args.add("--delete-attachment");
        args.add("name:" + name);
        args.add(file.getAbsolutePath());
        Launcher.exec(args, true);
    }

    private void writeAll() {
        List<String> args = new ArrayList<>();
        args.add("mkvpropedit");
        if (doTitle) {
            args.add("--set");
            args.add("title=" + title);
        }
        if (doCover) {
            args.add("--add-attachment");
            args.add(largeCover.getAbsolutePath());
            args.add("--add-attachment");
            args.add(smallCover.getAbsolutePath());
        }
        args.add(file.getAbsolutePath());
        Launcher.exec(args);
    }

    public void write() {
        if (doCover) {
            deleteAttachmentWithName(LARGE_COVER_PORT_NAME);
            deleteAttachmentWithName(LARGE_COVER_LAND_NAME);
            deleteAttachmentWithName(SMALL_COVER_PORT_NAME);
            deleteAttachmentWithName(SMALL_COVER_LAND_NAME);
        }
        writeAll();
    }
}

