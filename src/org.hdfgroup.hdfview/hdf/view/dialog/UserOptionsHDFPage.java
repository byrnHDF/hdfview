/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the COPYING file, which can be found  *
 * at the root of the source code distribution tree,                         *
 * or in https://www.hdfgroup.org/licenses.                                  *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package hdf.view.dialog;

import java.awt.GraphicsEnvironment;
import java.io.File;

import hdf.view.Tools;
import hdf.view.ViewProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * UserOptionsHDFPage.java - Configuration page for HDF-specific application
 * settings.
 */
public class UserOptionsHDFPage extends UserOptionsDefaultPage {
    private static final Logger log = LoggerFactory.getLogger(UserOptionsHDFPage.class);

    private Text fileExtField;
    private Combo pluginCombo;
    private Button clearTextButton, deleteTextButton, insertTextButton;
    private Button checkConvertEnum, checkShowRegRefValues, helpButton;
    private Button checkNativeOrder, checkDecOrder, checkIncOrder;
    private Button checkIndexName, checkIndexCreateOrder;
    private Button earlyLibVersion, early18LibVersion, early110LibVersion, early112LibVersion,
        early114LibVersion, early200LibVersion, earlyLateLibVersion;
    private Button lateLibVersion, late18LibVersion, late110LibVersion, late112LibVersion, late114LibVersion,
        late200LibVersion, lateLateLibVersion;
    private Button pluginDirButton;

    /** Default early libversion for files */
    private static String earlyLibVers;

    /** Default late libversion for files */
    private static String lateLibVers;

    /** Default index type for files */
    private static String indexType;

    /** Default index ordering for files */
    private static String indexOrder;

    /** Path to plugins */
    private String pluginDir            = null;
    private int pluginDirIndex          = -1;
    private boolean isPluginListChanged = false;

    /** a list of plugin paths */
    private static String[] pluginPathList;

    /**
     * Configuration page for HDF-specific application settings.
     */
    public UserOptionsHDFPage()
    {
        super("HDF Settings");
        isPluginListChanged = false;
    }

    /**
     * Performs special processing when this page's Defaults button has been pressed.
     */
    @Override
    public void performDefaults()
    {
        super.performDefaults();
        getPreferenceStore();
    }

    /**
     * Notifies that the OK button of this page's container has been pressed.
     *
     * @return <code>false</code> to abort the container's OK processing and
     * <code>true</code> to allow the OK to happen
     */
    @Override
    public boolean performOk()
    {
        getPreferenceStore();

        if (fileExtField != null) {
            String ext = fileExtField.getText();
            if ((ext != null) && (ext.length() > 0)) {
                ext = ext.trim();
                ViewProperties.setFileExtension(ext);
            }
        }

        log.trace("performOk: save HDF options earlyLibVersion={}", earlyLibVersion);
        if (earlyLibVersion != null) {
            if (earlyLibVersion.getSelection())
                ViewProperties.setEarlyLib("Earliest");
            else if (early18LibVersion.getSelection())
                ViewProperties.setEarlyLib("v18");
            else if (early110LibVersion.getSelection())
                ViewProperties.setEarlyLib("v110");
            else if (early112LibVersion.getSelection())
                ViewProperties.setEarlyLib("v112");
            else if (early114LibVersion.getSelection())
                ViewProperties.setEarlyLib("v114");
            else if (early200LibVersion.getSelection())
                ViewProperties.setEarlyLib("v200");
            else if (earlyLateLibVersion.getSelection())
                ViewProperties.setEarlyLib("Latest");
            else
                ViewProperties.setEarlyLib("Earliest");
        }

        log.trace("performOk: save HDF options lateLibVersion={}", lateLibVersion);
        if (lateLibVersion != null) {
            if (lateLibVersion.getSelection())
                ViewProperties.setLateLib("Earliest");
            else if (late18LibVersion.getSelection())
                ViewProperties.setLateLib("v18");
            else if (late110LibVersion.getSelection())
                ViewProperties.setLateLib("v110");
            else if (late112LibVersion.getSelection())
                ViewProperties.setLateLib("v112");
            else if (late114LibVersion.getSelection())
                ViewProperties.setLateLib("v114");
            else if (late200LibVersion.getSelection())
                ViewProperties.setLateLib("v200");
            else if (lateLateLibVersion.getSelection())
                ViewProperties.setLateLib("Latest");
            else
                ViewProperties.setLateLib("Latest");
        }

        // set index type
        if (checkIndexName != null) {
            if (checkIndexName.getSelection())
                ViewProperties.setIndexType("H5_INDEX_NAME");
            else
                ViewProperties.setIndexType("H5_INDEX_CRT_ORDER");
        }

        // set index order
        if (checkIncOrder != null) {
            if (checkIncOrder.getSelection())
                ViewProperties.setIndexOrder("H5_ITER_INC");
            else if (checkNativeOrder.getSelection())
                ViewProperties.setIndexOrder("H5_ITER_NATIVE");
            else
                ViewProperties.setIndexOrder("H5_ITER_DEC");
        }

        if (checkConvertEnum != null)
            ViewProperties.setConvertEnum(checkConvertEnum.getSelection());
        if (checkShowRegRefValues != null)
            ViewProperties.setShowRegRefValue(checkShowRegRefValues.getSelection());

        return true;
    }

    /**
     * Checks if the location of the PluginDir changed.
     *
     * @return true if the plugin directory changed.
     */
    public boolean isPluginListChanged() { return isPluginListChanged; }

    /**
     * Loads all stored values in the <code>FieldEditor</code>s.
     */
    protected void load()
    {
        getPreferenceStore();

        try {
            curFont = new Font(Display.getCurrent(), ViewProperties.getFontType(),
                               ViewProperties.getFontSize(), SWT.NORMAL);
        }
        catch (Exception ex) {
            curFont = null;
        }

        fileExtField.setText(ViewProperties.getFileExtension());

        earlyLibVers = ViewProperties.getEarlyLib();
        log.trace("performOk: load HDF options earlyLibVers={}", earlyLibVers);
        earlyLibVersion.setSelection(earlyLibVers.compareTo("Earliest") == 0);
        early18LibVersion.setSelection(earlyLibVers.compareTo("v18") == 0);
        early110LibVersion.setSelection(earlyLibVers.compareTo("v110") == 0);
        early112LibVersion.setSelection(earlyLibVers.compareTo("v112") == 0);
        early114LibVersion.setSelection(earlyLibVers.compareTo("v114") == 0);
        early200LibVersion.setSelection(earlyLibVers.compareTo("v200") == 0);
        earlyLateLibVersion.setSelection(earlyLibVers.compareTo("Latest") == 0);

        lateLibVers = ViewProperties.getLateLib();
        log.trace("performOk: load HDF options lateLibVers={}", lateLibVers);
        lateLibVersion.setSelection(lateLibVers.compareTo("Earliest") == 0);
        late18LibVersion.setSelection(lateLibVers.compareTo("v18") == 0);
        late110LibVersion.setSelection(lateLibVers.compareTo("v110") == 0);
        late112LibVersion.setSelection(lateLibVers.compareTo("v112") == 0);
        late114LibVersion.setSelection(lateLibVers.compareTo("v114") == 0);
        late200LibVersion.setSelection(lateLibVers.compareTo("v200") == 0);
        lateLateLibVersion.setSelection(lateLibVers.compareTo("Latest") == 0);

        checkConvertEnum.setSelection(ViewProperties.isConvertEnum());
        checkShowRegRefValues.setSelection(ViewProperties.showRegRefValues());

        indexType = ViewProperties.getIndexType();
        checkIndexName.setSelection(indexType.compareTo("H5_INDEX_NAME") == 0);
        checkIndexCreateOrder.setSelection(indexType.compareTo("H5_INDEX_CRT_ORDER") == 0);

        indexOrder = ViewProperties.getIndexOrder();
        checkIncOrder.setSelection(indexOrder.compareTo("H5_ITER_INC") == 0);
        checkDecOrder.setSelection(indexOrder.compareTo("H5_ITER_DEC") == 0);
        checkNativeOrder.setSelection(indexOrder.compareTo("H5_ITER_NATIVE") == 0);

        pluginPathList = ViewProperties.loadPluginPaths();
        pluginCombo.setItems(pluginPathList);
        pluginCombo.setVisibleItemCount(pluginPathList.length);
        pluginCombo.setText("");
        pluginCombo.deselectAll();
        pluginDirIndex = -1;
        pluginDir      = null;
    }

    /**
     * Creates and returns the SWT control for the customized body of this
     * preference page under the given parent composite.
     *
     * @param parent the parent composite
     * @return the new control
     */
    @Override
    protected Control createContents(Composite parent)
    {
        shell               = parent.getShell();
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        org.eclipse.swt.widgets.Group fileExtensionGroup =
            new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
        fileExtensionGroup.setLayout(new GridLayout(2, false));
        fileExtensionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fileExtensionGroup.setFont(curFont);
        fileExtensionGroup.setText("File Extensions");

        Label label = new Label(fileExtensionGroup, SWT.RIGHT);
        label.setFont(curFont);
        label.setText("Extensions: ");

        fileExtField = new Text(fileExtensionGroup, SWT.SINGLE | SWT.BORDER);
        fileExtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fileExtField.setFont(curFont);

        org.eclipse.swt.widgets.Group defaultLibVersionGroup =
            new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
        defaultLibVersionGroup.setLayout(new GridLayout());
        defaultLibVersionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        defaultLibVersionGroup.setFont(curFont);
        defaultLibVersionGroup.setText("Default Lib Version");

        org.eclipse.swt.widgets.Group earlyLibVersionGroup =
            new org.eclipse.swt.widgets.Group(defaultLibVersionGroup, SWT.NONE);
        earlyLibVersionGroup.setLayout(new GridLayout(4, true));
        earlyLibVersionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        earlyLibVersionGroup.setFont(curFont);
        earlyLibVersionGroup.setText("Default Early Lib Version");

        earlyLibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        earlyLibVersion.setFont(curFont);
        earlyLibVersion.setText("Earliest");
        earlyLibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        early18LibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        early18LibVersion.setFont(curFont);
        early18LibVersion.setText("v18");
        early18LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        early110LibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        early110LibVersion.setFont(curFont);
        early110LibVersion.setText("v110");
        early110LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        early112LibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        early112LibVersion.setFont(curFont);
        early112LibVersion.setText("v112");
        early112LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        early114LibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        early114LibVersion.setFont(curFont);
        early114LibVersion.setText("v114");
        early114LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        early200LibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        early200LibVersion.setFont(curFont);
        early200LibVersion.setText("v200");
        early200LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        earlyLateLibVersion = new Button(earlyLibVersionGroup, SWT.RADIO);
        earlyLateLibVersion.setFont(curFont);
        earlyLateLibVersion.setText("Latest");
        earlyLateLibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        org.eclipse.swt.widgets.Group lateLibVersionGroup =
            new org.eclipse.swt.widgets.Group(defaultLibVersionGroup, SWT.NONE);
        lateLibVersionGroup.setLayout(new GridLayout(4, true));
        lateLibVersionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        lateLibVersionGroup.setFont(curFont);
        lateLibVersionGroup.setText("Default Late Lib Version");

        lateLibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        lateLibVersion.setFont(curFont);
        lateLibVersion.setText("Earliest");
        lateLibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        late18LibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        late18LibVersion.setFont(curFont);
        late18LibVersion.setText("v18");
        late18LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        late110LibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        late110LibVersion.setFont(curFont);
        late110LibVersion.setText("v110");
        late110LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        late112LibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        late112LibVersion.setFont(curFont);
        late112LibVersion.setText("v112");
        late112LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        late114LibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        late114LibVersion.setFont(curFont);
        late114LibVersion.setText("v114");
        late114LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        late200LibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        late200LibVersion.setFont(curFont);
        late200LibVersion.setText("v200");
        late200LibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        lateLateLibVersion = new Button(lateLibVersionGroup, SWT.RADIO);
        lateLateLibVersion.setFont(curFont);
        lateLateLibVersion.setText("Latest");
        lateLateLibVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        org.eclipse.swt.widgets.Group dataGroup = new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
        dataGroup.setLayout(new GridLayout(4, false));
        dataGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        dataGroup.setFont(curFont);
        dataGroup.setText("Data");

        helpButton = new Button(dataGroup, SWT.PUSH);
        helpButton.setImage(ViewProperties.getHelpIcon());
        helpButton.setToolTipText("Help on Convert Enum");
        helpButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final String msg = "Convert enum data to strings. \n"
                                   + "For example, a dataset of an enum type of (R=0, G=, B=2) \n"
                                   +
                                   "has values of (0, 2, 2, 2, 1, 1). With conversion, the data values are \n"
                                   + "shown as (R, B, B, B, G, G).\n\n\n";

                Tools.showInformation(getShell(), "Help", msg);
            }
        });

        checkConvertEnum = new Button(dataGroup, SWT.CHECK);
        checkConvertEnum.setFont(curFont);
        checkConvertEnum.setText("Convert Enum");
        checkConvertEnum.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        checkShowRegRefValues = new Button(dataGroup, SWT.CHECK);
        checkShowRegRefValues.setFont(curFont);
        checkShowRegRefValues.setText("Show RegRef Values");
        checkShowRegRefValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        org.eclipse.swt.widgets.Group displayIndexingGroup =
            new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
        displayIndexingGroup.setLayout(new GridLayout());
        displayIndexingGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        displayIndexingGroup.setFont(curFont);
        displayIndexingGroup.setText("Display Indexing Options");

        org.eclipse.swt.widgets.Group indexingTypeGroup =
            new org.eclipse.swt.widgets.Group(displayIndexingGroup, SWT.NONE);
        indexingTypeGroup.setLayout(new GridLayout(2, true));
        indexingTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        indexingTypeGroup.setFont(curFont);
        indexingTypeGroup.setText("Indexing Type");

        checkIndexName = new Button(indexingTypeGroup, SWT.RADIO);
        checkIndexName.setFont(curFont);
        checkIndexName.setText("By Name");
        checkIndexName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        checkIndexCreateOrder = new Button(indexingTypeGroup, SWT.RADIO);
        checkIndexCreateOrder.setFont(curFont);
        checkIndexCreateOrder.setText("By Creation Order");
        checkIndexCreateOrder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        org.eclipse.swt.widgets.Group indexingOrderGroup =
            new org.eclipse.swt.widgets.Group(displayIndexingGroup, SWT.NONE);
        indexingOrderGroup.setLayout(new GridLayout(3, true));
        indexingOrderGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        indexingOrderGroup.setFont(curFont);
        indexingOrderGroup.setText("Indexing Order");

        checkIncOrder = new Button(indexingOrderGroup, SWT.RADIO);
        checkIncOrder.setFont(curFont);
        checkIncOrder.setText("Increments");
        checkIncOrder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        checkDecOrder = new Button(indexingOrderGroup, SWT.RADIO);
        checkDecOrder.setFont(curFont);
        checkDecOrder.setText("Decrements");
        checkDecOrder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        checkNativeOrder = new Button(indexingOrderGroup, SWT.RADIO);
        checkNativeOrder.setFont(curFont);
        checkNativeOrder.setText("Native");
        checkNativeOrder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        org.eclipse.swt.widgets.Group pluginDirectoryGroup =
            new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
        pluginDirectoryGroup.setLayout(new GridLayout(3, false));
        pluginDirectoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        pluginDirectoryGroup.setFont(curFont);
        pluginDirectoryGroup.setText("Plugin Paths");

        pluginPathList = ViewProperties.loadPluginPaths();
        pluginCombo    = new Combo(pluginDirectoryGroup, SWT.BORDER | SWT.SINGLE);
        pluginCombo.setFont(curFont);
        pluginCombo.setItems(pluginPathList);
        pluginCombo.setVisibleItemCount(pluginPathList.length);
        pluginCombo.deselectAll();
        pluginCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        pluginCombo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.keyCode == SWT.CR) {
                    String dirname = pluginCombo.getText();
                    if (dirname == null || dirname.length() < 1 || dirname.equals(pluginDir))
                        return;
                    pluginCombo.setText(dirname);
                }
            }
        });
        pluginCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                pluginDirIndex = pluginCombo.getSelectionIndex();
                pluginDir      = pluginCombo.getItem(pluginDirIndex);
            }
        });

        pluginDirButton = new Button(pluginDirectoryGroup, SWT.PUSH);
        pluginDirButton.setFont(curFont);
        pluginDirButton.setText("Browse...");
        pluginDirButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        pluginDirButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final DirectoryDialog dChooser = new DirectoryDialog(shell);
                dChooser.setFilterPath(System.getProperty("user.dir"));
                dChooser.setText("Select a Directory");

                String dir = dChooser.open();

                if (dir == null)
                    return;

                pluginCombo.setText(dir);
            }
        });

        insertTextButton = new Button(pluginDirectoryGroup, SWT.PUSH);
        insertTextButton.setFont(curFont);
        insertTextButton.setToolTipText("Append current selection");
        insertTextButton.setText("Add Path");
        insertTextButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        insertTextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String dirname = pluginCombo.getText();
                if (dirname == null || dirname.length() < 1) {
                    return;
                }
                ViewProperties.appendPluginPath(dirname);
                pluginPathList = ViewProperties.loadPluginPaths();
                pluginCombo.setItems(pluginPathList);
                pluginCombo.setVisibleItemCount(pluginPathList.length);
                pluginCombo.setText("");
                pluginCombo.deselectAll();
                pluginDirIndex = -1;
                pluginDir      = null;
            }
        });

        deleteTextButton = new Button(pluginDirectoryGroup, SWT.PUSH);
        deleteTextButton.setFont(curFont);
        deleteTextButton.setToolTipText("Remove current selection");
        deleteTextButton.setText("Remove Path");
        deleteTextButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        deleteTextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String dirname = pluginCombo.getText();
                if (dirname == null || dirname.length() < 1) {
                    return;
                }
                String[] items = pluginCombo.getItems();
                for (int idx = 0; idx < items.length; idx++) {
                    if (items[idx].equals(dirname)) {
                        pluginDirIndex = idx;
                    }
                }
                ViewProperties.deletePluginPath(pluginDirIndex);
                pluginPathList = ViewProperties.loadPluginPaths();
                pluginCombo.setItems(pluginPathList);
                pluginCombo.setVisibleItemCount(pluginPathList.length);
                pluginCombo.setText("");
                pluginCombo.deselectAll();
                pluginDirIndex = -1;
                pluginDir      = null;
            }
        });

        clearTextButton = new Button(pluginDirectoryGroup, SWT.PUSH);
        clearTextButton.setFont(curFont);
        clearTextButton.setToolTipText("Clear current selection");
        clearTextButton.setText("Clear Path Text");
        clearTextButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        clearTextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                pluginCombo.setText("");
                pluginCombo.deselectAll();
                pluginDirIndex = -1;
                pluginDir      = null;
            }
        });

        // shell.pack();

        log.info("UserOptionsHDFPage: plugin combobox created");

        load();
        return composite;
    }
}
