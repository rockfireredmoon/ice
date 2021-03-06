package org.iceui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public abstract class XFileSelector {

    static class JFileChooserSelector extends XFileSelector {

        private JFileChooser chooser;

        JFileChooserSelector(String dir) {
            chooser = new JFileChooser(dir);
        }

        @Override
        public void setMultiSelectionEnabled(boolean multiSelection) {
            chooser.setMultiSelectionEnabled(multiSelection);
        }

        @Override
        public void setFileSelectionMode(int fileSelectionMode) {
            chooser.setFileSelectionMode(fileSelectionMode);
        }

        @Override
        public void setSelectedFile(File file) {
            chooser.setSelectedFile(file);
        }

        @Override
        public void setCurrentDirectory(File file) {
            chooser.setCurrentDirectory(file);
        }

        @Override
        public int showDialog(Component parent, String title) {
            return chooser.showDialog(parent, title);
        }

        @Override
        public File getSelectedFile() {
            return chooser.getSelectedFile();
        }

        @Override
        public File getCurrentDirectory() {
            return chooser.getCurrentDirectory();
        }

        @Override
        public File[] getSelectedFiles() {
            return chooser.getSelectedFiles();
        }

        @Override
        public void setDialogType(int type) {
            chooser.setDialogType(type);

        }
    }

    static class AWTFileSelector extends XFileSelector {

        private FileDialog dialog;
        private boolean multiSelection;
        private int fileSelectionMode;
        private int dialogType;
        private File selectedFile;
        private File selectedDirectory;

        AWTFileSelector(String dir) {
            selectedDirectory = dir == null ? null : new File(dir);
        }

        @Override
        public void setMultiSelectionEnabled(boolean multiSelection) {
            this.multiSelection = multiSelection;
            doSetMultipleMode(multiSelection);
        }

        private void doSetMultipleMode(boolean multiSelection) {
            if (dialog != null) {
                try {
                    dialog.getClass()
                            .getMethod("setMultipleMode", boolean.class)
                            .invoke(dialog, multiSelection);
                } catch (Exception e) {
                }
            }
        }

        @Override
        public void setFileSelectionMode(int fileSelectionMode) {
            this.fileSelectionMode = fileSelectionMode;
            createFilter();
        }

        void createFilter() {
            if (dialog != null) {
                switch (fileSelectionMode) {
                    case DIRECTORIES_ONLY:
                        dialog.setFilenameFilter(new FilenameFilter() {
                            public boolean accept(File arg0, String arg1) {
                                return arg0.isDirectory();
                            }
                        });
                        break;
                    case FILES_ONLY:
                        dialog.setFilenameFilter(new FilenameFilter() {
                            public boolean accept(File arg0, String arg1) {
                                return arg0.isFile();
                            }
                        });
                        break;
                    default:
                        dialog.setFilenameFilter(new FilenameFilter() {
                            public boolean accept(File arg0, String arg1) {
                                return true;
                            }
                        });
                        break;
                }
            }
        }

        @Override
        public void setSelectedFile(File file) {
            this.selectedFile = file;
            doSetSelectedFile();

        }

        private void doSetSelectedFile() {
            if (dialog != null) {
                String path = selectedFile == null ? null : selectedFile
                        .getPath();
                dialog.setFile(path);
            }
        }

        @Override
        public void setCurrentDirectory(File file) {
            this.selectedDirectory = file;
            doSetCurrentDirectory();
        }

        private void doSetCurrentDirectory() {
            if (dialog != null) {
                dialog.setDirectory(selectedDirectory == null ? null
                        : selectedDirectory.getAbsolutePath());
            }
        }

        @Override
        public int showDialog(Component parent, String title) {
            Window window = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
            if (window instanceof Frame) {
                dialog = new FileDialog((Frame) window);
            } else if (window instanceof Dialog) {
                dialog = new FileDialog((Dialog) window);
            } else {
                dialog = new FileDialog((Dialog) null);
            }
            createFilter();
            doSetCurrentDirectory();
            doSetSelectedFile();
            doSetMultipleMode(multiSelection);
            doSetDialogType();
            dialog.setVisible(true);
            if (dialog.getFile() == null) {
                selectedFile = null;
                return CANCEL_OPTION;
            }
            return APPROVE_OPTION;
        }

        @Override
        public File getSelectedFile() {
            return dialog == null || dialog.getFile() == null ? selectedFile
                    : new File(dialog.getFile());
        }

        @Override
        public File getCurrentDirectory() {
            return dialog == null || dialog.getDirectory() == null ? selectedDirectory
                    : new File(dialog.getDirectory());
        }

        @Override
        public File[] getSelectedFiles() {
            if (dialog != null) {
                try {
                    return (File[]) dialog.getClass().getMethod("getFiles")
                            .invoke(dialog);
                } catch (Exception e) {
                }
            }
            File f = getSelectedFile();
            if (f != null) {
                return new File[]{f};
            }
            return null;
        }

        @Override
        public void setDialogType(int type) {
            this.dialogType = type;
            doSetDialogType();

        }

        private void doSetDialogType() {
            if (dialog != null) {
                dialog.setMode(dialogType == OPEN_DIALOG ? FileDialog.LOAD
                        : FileDialog.SAVE);
            }
        }
    }
    // These are same as JFileChooser for ease of implementation
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    public static final int ERROR_OPTION = -1;
    public static final int FILES_ONLY = 0;
    public static final int DIRECTORIES_ONLY = 1;
    public static final int FILES_AND_DIRECTORIES = 2;
    public static final int OPEN_DIALOG = 0;
    public static final int SAVE_DIALOG = 1;

    public void open() {
    }

    public static XFileSelector create(String dir) {
//        if (SystemUtils.IS_OS_LINUX && !SystemUtils.IS_JAVA_1_6
//                && !SystemUtils.IS_JAVA_1_5 && !SystemUtils.IS_JAVA_1_4) {
//            return new AWTFileSelector(dir);
//        } else {
            return new JFileChooserSelector(dir);
//        }
    }

    public abstract void setMultiSelectionEnabled(boolean multiSelection);

    public abstract void setFileSelectionMode(int fileSelectionMode);

    public abstract void setSelectedFile(File file);

    public abstract void setCurrentDirectory(File file);

    public abstract int showDialog(Component parent, String title);

    public abstract File getSelectedFile();

    public abstract File getCurrentDirectory();

    public abstract File[] getSelectedFiles();

    public abstract void setDialogType(int openDialog);
}
