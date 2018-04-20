/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the files COPYING and Copyright.html. *
 * COPYING can be found at the root of the source code distribution tree.    *
 * Or, see https://support.hdfgroup.org/products/licenses.html               *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package hdf.view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.validate.DataValidator;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.KeyEditAction;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.edit.config.DialogErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectAllCommand;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.BodyCellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import hdf.object.CompoundDS;
import hdf.object.DataFormat;
import hdf.object.Dataset;
import hdf.object.Datatype;
import hdf.object.FileFormat;
import hdf.object.Group;
import hdf.object.HObject;
import hdf.object.ScalarDS;
import hdf.view.ViewProperties.BITMASK_OP;
import hdf.view.dialog.InputDialog;
import hdf.view.dialog.MathConversionDialog;
import hdf.view.dialog.NewDatasetDialog;

/**
 * TODO:
 *
 * @author jhenderson
 * @version 1.0 4/13/2018
 */
public abstract class DefaultBaseTableView implements TableView {

    private final static org.slf4j.Logger   log = org.slf4j.LoggerFactory.getLogger(DefaultBaseTableView.class);

    private final Display                   display = Display.getDefault();
    protected final Shell                   shell;
    protected Font                          curFont;

    // The main HDFView
    protected final ViewManager             viewer;

    protected NatTable                      dataTable;

    // The data object to be displayed in the Table
    protected final DataFormat              dataObject;

    // The data value of the data object
    protected Object                        dataValue;

    protected Object                        fillValue;

    protected enum ViewType { TABLE, IMAGE };
    protected      ViewType                 viewType = ViewType.TABLE;

    /**
     * Numerical data type. B = byte array, S = short array, I = int array, J = long array, F =
     * float array, and D = double array.
     */
    protected char                          NT = ' ';

    // Changed to use normalized scientific notation (1 <= coefficient < 10).
    // private final DecimalFormat scientificFormat = new DecimalFormat("###.#####E0#");
    protected final DecimalFormat           scientificFormat = new DecimalFormat("0.0###E0###");
    protected DecimalFormat                 customFormat     = new DecimalFormat("###.#####");
    protected final NumberFormat            normalFormat     = null;
    protected NumberFormat                  numberFormat     = normalFormat;

    // Used for bitmask operations on data
    protected BitSet                        bitmask = null;
    protected BITMASK_OP                    bitmaskOP = BITMASK_OP.EXTRACT;

    // Fields to keep track of which 'frame' of 3 dimensional data is being displayed
    private Text                            frameField;
    private long                            curDataFrame = 0;
    private long                            maxDataFrame = 1;

    // The index base used for display row and column numbers of data
    protected int                           indexBase = 0;

    protected int                           fixedDataLength = -1;

    protected int                           binaryOrder;

    protected boolean                       isReadOnly = false;

    protected boolean                       isValueChanged = false;

    protected boolean                       isEnumConverted = false;

    protected boolean                       isDisplayTypeChar, isDataTransposed;

    protected boolean                       isRegRef = false, isObjRef = false;
    protected boolean                       showAsHex = false, showAsBin = false;

    // Keep references to the selection and data layers for ease of access
    protected SelectionLayer                selectionLayer;
    protected DataLayer                     dataLayer;

    protected IDataProvider                 rowHeaderDataProvider;
    protected IDataProvider                 columnHeaderDataProvider;

    protected IDisplayConverter             dataDisplayConverter;

    /**
     * Global variables for GUI components
     */

    protected MenuItem                      checkFixedDataLength = null;
    protected MenuItem                      checkCustomNotation = null;
    protected MenuItem                      checkScientificNotation = null;
    protected MenuItem                      checkHex = null;
    protected MenuItem                      checkBin = null;

    // Labeled Group to display the index base
    protected org.eclipse.swt.widgets.Group indexBaseGroup;

    // Text field to display the value of the currently selected table cell
    protected Text                          cellValueField;

    // Label to indicate the current cell location
    protected Label                         cellLabel;


    /**
     * Constructs a base TableView with no additional data properties.
     *
     * @param theView
     *            the main HDFView.
     */
    public DefaultBaseTableView(ViewManager theView) {
        this(theView, null);
    }

    /**
     * Constructs a base TableView with the specified data properties.
     *
     * @param theView
     *            the main HDFView.
     *
     * @param dataPropertiesMap
     *            the properties on how to show the data. The map is used to allow
     *            applications to pass properties on how to display the data, such
     *            as: transposing data, showing data as characters, applying a
     *            bitmask, and etc. Predefined keys are listed at
     *            ViewProperties.DATA_VIEW_KEY.
     */
    @SuppressWarnings("rawtypes")
    public DefaultBaseTableView(ViewManager theView, HashMap dataPropertiesMap) {
        log.trace("start");

        shell = new Shell(display, SWT.SHELL_TRIM);

        shell.setData(this);

        shell.setLayout(new GridLayout(1, true));

        /*
         * When the table is closed, make sure to prompt the user about saving their
         * changes, then do any pending cleanup work.
         */
        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (isValueChanged && !isReadOnly) {
                    if (MessageDialog.openConfirm(shell, "Changes Detected", "\"" + ((HObject) dataObject).getName()
                            + "\" has changed.\nDo you want to save the changes?"))
                        updateValueInFile();
                    else
                        dataObject.clearData();
                }

                dataValue = null;
                dataTable = null;

                if (curFont != null) curFont.dispose();

                viewer.removeDataView(DefaultBaseTableView.this);
            }
        });

        /* Grab the current font to be used for all GUI components */
        try {
            curFont = new Font(display, ViewProperties.getFontType(), ViewProperties.getFontSize(), SWT.NORMAL);
        }
        catch (Exception ex) {
            curFont = null;
        }

        viewer = theView;

        /* Retrieve any display properties passed in via the HashMap parameter */
        HObject hObject = null;

        if (ViewProperties.isIndexBase1()) indexBase = 1;

        if (dataPropertiesMap != null) {
            hObject = (HObject) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.OBJECT);

            bitmask = (BitSet) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.BITMASK);
            bitmaskOP = (BITMASK_OP) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.BITMASKOP);

            Boolean b = (Boolean) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.CHAR);
            if (b != null) isDisplayTypeChar = b.booleanValue();

            b = (Boolean) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.TRANSPOSED);
            if (b != null) isDataTransposed = b.booleanValue();

            b = (Boolean) dataPropertiesMap.get(ViewProperties.DATA_VIEW_KEY.INDEXBASE1);
            if (b != null) {
                if (b.booleanValue())
                    indexBase = 1;
                else
                    indexBase = 0;
            }
        }

        log.trace("Index base = {} - Is data transposed = {} - Is display type char = {}",
                indexBase, isDataTransposed,
                isDisplayTypeChar);

        if (hObject == null) hObject = viewer.getTreeView().getCurrentObject();

        dataObject = (DataFormat) hObject;

        /* Only edit objects which actually contain editable data */
        if ((dataObject == null) || !(dataObject instanceof DataFormat)) {
            log.debug("data object is null or not an instanceof DataFormat");
            log.trace("finish");
            return;
        }

        isReadOnly = ((HObject) dataObject).getFileFormat().isReadOnly();

        log.trace("dataObject({}) isReadOnly={}", dataObject, isReadOnly);

        long[] dims = dataObject.getDims();
        long tsize = 1;

        if (dims == null) {
            log.debug("data object has null dimensions");
            log.trace("finish");
            Tools.showError(shell, "Could not open data object '" + ((HObject) dataObject).getName()
                    + "'. Data object has null dimensions.", shell.getText());
            return;
        }

        for (int i = 0; i < dims.length; i++)
            tsize *= dims[i];

        log.trace("Data object Size={} Height={} Width={}", tsize, dataObject.getHeight(),
                dataObject.getWidth());

        if (dataObject.getHeight() <= 0 || dataObject.getWidth() <= 0 || tsize <= 0) {
            log.debug("data object has dimension of size 0");
            log.trace("finish");
            Tools.showError(shell, "Could not open data object '" + ((HObject) dataObject).getName()
                    + "'. Data object has dimension of size 0.", shell.getText());
            return;
        }

        // Cannot edit HDF4 Vdata
        if (((HObject) dataObject).getFileFormat().isThisType(FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF4))
                && (dataObject instanceof CompoundDS)) {
            isReadOnly = true;
        }

        // Disable edit feature for SZIP compression when encode is not enabled
        if (!isReadOnly) {
            String compression = dataObject.getCompression();
            if ((compression != null) && compression.startsWith("SZIP")) {
                if (!compression.endsWith("ENCODE_ENABLED")) {
                    isReadOnly = true;
                }
            }
        }

        /*
         * Determine whether the data is to be displayed as characters and whether or
         * not enum data is to be converted.
         */
        Datatype dtype = dataObject.getDatatype();

        log.trace("Data object getDatatypeClass()={}", dtype.getDatatypeClass());
        isDisplayTypeChar = (isDisplayTypeChar
                && (dtype.getDatatypeSize() == 1 || (dtype.getDatatypeClass() == Datatype.CLASS_ARRAY
                && dtype.getBasetype().getDatatypeClass() == Datatype.CLASS_CHAR)));

        isEnumConverted = ViewProperties.isConvertEnum();

        log.trace("Data object isDisplayTypeChar={} isEnumConverted={}", isDisplayTypeChar,
                isEnumConverted);

        // Setup subset information
        log.trace("Setup subset information");

        int rank = dataObject.getRank();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long[] count = dataObject.getSelectedDims();
        long[] stride = dataObject.getStride();
        long[] start = dataObject.getStartDims();
        int n = Math.min(3, rank);

        if (rank > 2) {
            curDataFrame = start[selectedIndex[2]] + indexBase;
            maxDataFrame = (indexBase == 1) ? dims[selectedIndex[2]] : dims[selectedIndex[2]] - 1;
        }

        /* Create the toolbar area that contains useful shortcuts */
        ToolBar toolBar = createToolbar(shell);
        toolBar.setSize(shell.getSize().x, 30);
        toolBar.setLocation(0, 0);

        /*
         * Create the group that contains the text fields for displaying the value and
         * location of the current cell, as well as the index base.
         */
        indexBaseGroup = new org.eclipse.swt.widgets.Group(shell, SWT.SHADOW_ETCHED_OUT);
        indexBaseGroup.setFont(curFont);
        indexBaseGroup.setText(indexBase + "-based");
        indexBaseGroup.setLayout(new GridLayout(1, true));
        indexBaseGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        SashForm content = new SashForm(indexBaseGroup, SWT.VERTICAL);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setSashWidth(10);

        SashForm cellValueComposite = new SashForm(content, SWT.HORIZONTAL);
        cellValueComposite.setSashWidth(8);

        cellLabel = new Label(cellValueComposite, SWT.RIGHT | SWT.BORDER);
        cellLabel.setAlignment(SWT.CENTER);
        cellLabel.setFont(curFont);

        cellValueField = new Text(cellValueComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        cellValueField.setEditable(false);
        cellValueField.setBackground(new Color(display, 255, 255, 240));
        cellValueField.setEnabled(false);
        cellValueField.setFont(curFont);

        cellValueComposite.setWeights(new int[] { 1, 5 });

        /* Create the Shell's MenuBar */
        shell.setMenuBar(createMenuBar(shell));

        /* Create the actual NatTable */
        try {
            dataTable = createTable(content, dataObject);
            if (dataTable == null) {
                log.debug("table creation for object '" + ((HObject) dataObject).getName() + "' failed");
                log.trace("finish");
                viewer.showStatus("Creating table for object '" + ((HObject) dataObject).getName() + "' failed.");
                shell.dispose();
                return;
            }
        }
        catch (UnsupportedOperationException ex) {
            log.debug("Subclass does not implement createTable()");
            log.trace("finish");
            return;
        }

        dataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        /*
         * Set the Shell's title using the object path and name
         */
        StringBuffer sb = new StringBuffer(hObject.getName());
        sb.append("  at  ");
        sb.append(hObject.getPath());
        sb.append("  [");
        sb.append(((HObject) dataObject).getFileFormat().getName());
        sb.append("  in  ");
        sb.append(((HObject) dataObject).getFileFormat().getParent());
        sb.append("]");

        shell.setText(sb.toString());

        /*
         * Append subsetting information and show this as a status message in the
         * HDFView main window
         */
        sb.append(" [ dims");
        sb.append(selectedIndex[0]);
        for (int i = 1; i < n; i++) {
            sb.append("x");
            sb.append(selectedIndex[i]);
        }
        sb.append(", start");
        sb.append(start[selectedIndex[0]]);
        for (int i = 1; i < n; i++) {
            sb.append("x");
            sb.append(start[selectedIndex[i]]);
        }
        sb.append(", count");
        sb.append(count[selectedIndex[0]]);
        for (int i = 1; i < n; i++) {
            sb.append("x");
            sb.append(count[selectedIndex[i]]);
        }
        sb.append(", stride");
        sb.append(stride[selectedIndex[0]]);
        for (int i = 1; i < n; i++) {
            sb.append("x");
            sb.append(stride[selectedIndex[i]]);
        }
        sb.append(" ] ");

        log.trace("subset={}", sb.toString());

        viewer.showStatus(sb.toString());

        indexBaseGroup.pack();

        content.setWeights(new int[] { 1, 12 });

        shell.pack();

        int width = 700 + (ViewProperties.getFontSize() - 12) * 15;
        int height = 500 + (ViewProperties.getFontSize() - 12) * 10;
        shell.setSize(width, height);

        viewer.addDataView(this);

        log.trace("finish");

        shell.open();
    }

    /**
     * Creates the toolbar for the Shell.
     */
    private ToolBar createToolbar(final Shell shell) {
        ToolBar toolbar = new ToolBar(shell, SWT.HORIZONTAL | SWT.RIGHT | SWT.BORDER);
        toolbar.setFont(curFont);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        // Chart button
        ToolItem item = new ToolItem(toolbar, SWT.PUSH);
        item.setImage(ViewProperties.getChartIcon());
        item.setToolTipText("Line Plot");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showLineplot();
            }
        });

        if (dataObject.getRank() > 2) {
            new ToolItem(toolbar, SWT.SEPARATOR).setWidth(20);

            // First frame button
            item = new ToolItem(toolbar, SWT.PUSH);
            item.setImage(ViewProperties.getFirstIcon());
            item.setToolTipText("First Frame");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    firstFrame();
                }
            });

            // Previous frame button
            item = new ToolItem(toolbar, SWT.PUSH);
            item.setImage(ViewProperties.getPreviousIcon());
            item.setToolTipText("Previous Frame");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    previousFrame();
                }
            });

            ToolItem separator = new ToolItem(toolbar, SWT.SEPARATOR);

            frameField = new Text(toolbar, SWT.SINGLE | SWT.BORDER | SWT.CENTER);
            frameField.setFont(curFont);
            frameField.setText(String.valueOf(curDataFrame));
            frameField.addTraverseListener(new TraverseListener() {
                @Override
                public void keyTraversed(TraverseEvent e) {
                    if (e.detail == SWT.TRAVERSE_RETURN) {
                        try {
                            int frame = 0;

                            try {
                                frame = Integer.parseInt(frameField.getText().trim()) - indexBase;
                            }
                            catch (Exception ex) {
                                frame = -1;
                            }

                            gotoFrame(frame);
                        }
                        catch (Exception ex) {
                            log.debug("Frame change failure: ", ex);
                        }
                    }
                }
            });

            frameField.pack();

            separator.setWidth(frameField.getSize().x + 30);
            separator.setControl(frameField);

            separator = new ToolItem(toolbar, SWT.SEPARATOR);

            Text maxFrameText = new Text(toolbar, SWT.SINGLE | SWT.BORDER | SWT.CENTER);
            maxFrameText.setFont(curFont);
            maxFrameText.setText(String.valueOf(maxDataFrame));
            maxFrameText.setEditable(false);
            maxFrameText.setEnabled(false);

            maxFrameText.pack();

            separator.setWidth(maxFrameText.getSize().x + 30);
            separator.setControl(maxFrameText);

            new ToolItem(toolbar, SWT.SEPARATOR).setWidth(10);

            // Next frame button
            item = new ToolItem(toolbar, SWT.PUSH);
            item.setImage(ViewProperties.getNextIcon());
            item.setToolTipText("Next Frame");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    nextFrame();
                }
            });

            // Last frame button
            item = new ToolItem(toolbar, SWT.PUSH);
            item.setImage(ViewProperties.getLastIcon());
            item.setToolTipText("Last Frame");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    lastFrame();
                }
            });
        }

        return toolbar;
    }

    /**
     * Creates the menubar for the Shell.
     */
    private Menu createMenuBar(final Shell theShell) {
        Menu menuBar = new Menu(theShell, SWT.BAR);
        boolean isEditable = !isReadOnly;

        MenuItem tableMenu = new MenuItem(menuBar, SWT.CASCADE);
        tableMenu.setText("&Table");

        Menu menu = new Menu(theShell, SWT.DROP_DOWN);
        tableMenu.setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Export Data to Text File");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    saveAsText();
                }
                catch (Exception ex) {
                    theShell.getDisplay().beep();
                    Tools.showError(theShell, ex.getMessage(), theShell.getText());
                }
            }
        });

        if (dataObject instanceof ScalarDS) {
            MenuItem exportAsBinaryMenuItem = new MenuItem(menu, SWT.CASCADE);
            exportAsBinaryMenuItem.setText("Export Data to Binary File");

            Menu exportAsBinaryMenu = new Menu(menu);

            item = new MenuItem(exportAsBinaryMenu, SWT.PUSH);
            item.setText("Native Order");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 1;

                    try {
                        saveAsBinary();
                    }
                    catch (Exception ex) {
                        theShell.getDisplay().beep();
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            item = new MenuItem(exportAsBinaryMenu, SWT.PUSH);
            item.setText("Little Endian");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 2;

                    try {
                        saveAsBinary();
                    }
                    catch (Exception ex) {
                        theShell.getDisplay().beep();
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            item = new MenuItem(exportAsBinaryMenu, SWT.PUSH);
            item.setText("Big Endian");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 3;

                    try {
                        saveAsBinary();
                    }
                    catch (Exception ex) {
                        theShell.getDisplay().beep();
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            exportAsBinaryMenuItem.setMenu(exportAsBinaryMenu);
        }

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Import Data from Text File");
        item.setEnabled(isEditable);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String currentDir = ((HObject) dataObject).getFileFormat().getParent();

                String filename = null;
                if (((HDFView) viewer).getTestState()) {
                    filename = currentDir + File.separator + new InputDialog(theShell, "Enter a file name", "").open();
                }
                else {
                    FileDialog fChooser = new FileDialog(theShell, SWT.OPEN);
                    fChooser.setFilterPath(currentDir);

                    DefaultFileFilter filter = DefaultFileFilter.getFileFilterText();
                    fChooser.setFilterExtensions(new String[] { "*.*", filter.getExtensions() });
                    fChooser.setFilterNames(new String[] { "All Files", filter.getDescription() });
                    fChooser.setFilterIndex(1);

                    filename = fChooser.open();
                }

                if (filename == null) return;

                File chosenFile = new File(filename);
                if (!chosenFile.exists()) {
                    Tools.showError(theShell, "File " + filename + " does not exist.", "Import Data from Text File");
                    return;
                }

                if (!MessageDialog.openConfirm(theShell, "Import Data", "Do you want to paste selected data?")) return;
                importTextData(chosenFile.getAbsolutePath());
            }
        });

        if (dataObject instanceof ScalarDS) {
            checkFixedDataLength = new MenuItem(menu, SWT.CHECK);
            checkFixedDataLength.setText("Fixed Data Length");
            checkFixedDataLength.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!checkFixedDataLength.getSelection()) {
                        fixedDataLength = -1;
                        return;
                    }

                    String str = new InputDialog(theShell, "",
                            "Enter fixed data length when importing text data\n\n"
                                    + "For example, for a text string of \"12345678\"\n\t\tenter 2,"
                                    + "the data will be 12, 34, 56, 78\n\t\tenter 4, the data will be" + "1234, 5678\n")
                            .open();

                    if ((str == null) || (str.length() < 1)) {
                        checkFixedDataLength.setSelection(false);
                        return;
                    }

                    try {
                        fixedDataLength = Integer.parseInt(str);
                    }
                    catch (Exception ex) {
                        fixedDataLength = -1;
                    }

                    if (fixedDataLength < 1) {
                        checkFixedDataLength.setSelection(false);
                        return;
                    }
                }
            });

            MenuItem importAsBinaryMenuItem = new MenuItem(menu, SWT.CASCADE);
            importAsBinaryMenuItem.setText("Import Data from Binary File");

            Menu importFromBinaryMenu = new Menu(menu);

            item = new MenuItem(importFromBinaryMenu, SWT.PUSH);
            item.setText("Native Order");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 1;

                    try {
                        importBinaryData();
                    }
                    catch (Exception ex) {
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            item = new MenuItem(importFromBinaryMenu, SWT.PUSH);
            item.setText("Little Endian");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 2;

                    try {
                        importBinaryData();
                    }
                    catch (Exception ex) {
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            item = new MenuItem(importFromBinaryMenu, SWT.PUSH);
            item.setText("Big Endian");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    binaryOrder = 3;

                    try {
                        importBinaryData();
                    }
                    catch (Exception ex) {
                        Tools.showError(theShell, ex.getMessage(), theShell.getText());
                    }
                }
            });

            importAsBinaryMenuItem.setMenu(importFromBinaryMenu);
        }

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.setAccelerator(SWT.CTRL | 'C');
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                copyData();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Paste");
        item.setAccelerator(SWT.CTRL | 'V');
        item.setEnabled(isEditable);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pasteData();
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy to New Dataset");
        item.setEnabled(isEditable && (dataObject instanceof ScalarDS));
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ((selectionLayer.getSelectedColumnPositions().length <= 0)
                        || (selectionLayer.getSelectedRowCount() <= 0)) {
                    MessageDialog.openInformation(theShell, theShell.getText(), "Select table cells to write.");
                    return;
                }

                TreeView treeView = viewer.getTreeView();
                Group pGroup = (Group) (treeView.findTreeItem((HObject) dataObject).getParentItem().getData());
                HObject root = ((HObject) dataObject).getFileFormat().getRootObject();

                if (root == null) return;

                Vector<HObject> list = new Vector<>(((HObject) dataObject).getFileFormat().getNumberOfMembers() + 5);
                Iterator<HObject> it = ((Group) root).depthFirstMemberList().iterator();

                while (it.hasNext())
                    list.add(it.next());
                list.add(root);

                NewDatasetDialog dialog = new NewDatasetDialog(theShell, pGroup, list, DefaultBaseTableView.this);
                dialog.open();

                HObject obj = dialog.getObject();
                if (obj != null) {
                    Group pgroup = dialog.getParentGroup();
                    try {
                        treeView.addObject(obj, pgroup);
                    }
                    catch (Exception ex) {
                        log.debug("Write selection to dataset:", ex);
                    }
                }

                list.setSize(0);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Save Changes to File");
        item.setAccelerator(SWT.CTRL | 'U');
        item.setEnabled(isEditable);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    updateValueInFile();
                }
                catch (Exception ex) {
                    theShell.getDisplay().beep();
                    Tools.showError(theShell, ex.getMessage(), theShell.getText());
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Select All");
        item.setAccelerator(SWT.CTRL | 'A');
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    dataTable.doCommand(new SelectAllCommand());
                }
                catch (Exception ex) {
                    theShell.getDisplay().beep();
                    Tools.showError(theShell, ex.getMessage(), theShell.getText());
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Show Lineplot");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showLineplot();
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Show Statistics");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Object theData = getSelectedData();

                    if (dataObject instanceof CompoundDS) {
                        int cols = selectionLayer.getFullySelectedColumnPositions().length;
                        if (cols != 1) {
                            Tools.showError(theShell, "Please select one column at a time for compound dataset.",
                                    theShell.getText());
                            return;
                        }
                    }
                    else if (theData == null) {
                        theData = dataValue;
                    }

                    double[] minmax = new double[2];
                    double[] stat = new double[2];

                    Tools.findMinMax(theData, minmax, fillValue);
                    if (Tools.computeStatistics(theData, stat, fillValue) > 0) {
                        String stats = "Min                      = " + minmax[0] + "\nMax                      = "
                                + minmax[1] + "\nMean                     = " + stat[0] + "\nStandard deviation = "
                                + stat[1];
                        MessageDialog.openInformation(theShell, "Statistics", stats);
                    }

                    theData = null;
                    System.gc();
                }
                catch (Exception ex) {
                    theShell.getDisplay().beep();
                    Tools.showError(shell, ex.getMessage(), theShell.getText());
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Math Conversion");
        item.setEnabled(isEditable);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    mathConversion();
                }
                catch (Exception ex) {
                    shell.getDisplay().beep();
                    Tools.showError(theShell, ex.getMessage(), theShell.getText());
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        if (dataObject instanceof ScalarDS) {
            checkScientificNotation = new MenuItem(menu, SWT.CHECK);
            checkScientificNotation.setText("Show Scientific Notation");
            checkScientificNotation.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (checkScientificNotation.getSelection()) {
                        if (checkCustomNotation != null) checkCustomNotation.setSelection(false);
                        if (checkHex != null) checkHex.setSelection(false);
                        if (checkBin != null) checkBin.setSelection(false);

                        numberFormat = scientificFormat;
                        showAsHex = false;
                        showAsBin = false;
                    }
                    else {
                        numberFormat = normalFormat;
                    }

                    dataTable.doCommand(new VisualRefreshCommand());
                }
            });

            checkCustomNotation = new MenuItem(menu, SWT.CHECK);
            checkCustomNotation.setText("Show Custom Notation");
            checkCustomNotation.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (checkCustomNotation.getSelection()) {
                        if (checkScientificNotation != null) checkScientificNotation.setSelection(false);
                        if (checkHex != null) checkHex.setSelection(false);
                        if (checkBin != null) checkBin.setSelection(false);

                        numberFormat = customFormat;
                        showAsHex = false;
                        showAsBin = false;
                    }
                    else {
                        numberFormat = normalFormat;
                    }

                    dataTable.doCommand(new VisualRefreshCommand());
                }
            });
        }

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Create custom notation");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String msg = "Create number format by pattern \nINTEGER . FRACTION E EXPONENT\nusing # for optional digits and 0 for required digits"
                        + "\nwhere, INTEGER: the pattern for the integer part"
                        + "\n       FRACTION: the pattern for the fractional part"
                        + "\n       EXPONENT: the pattern for the exponent part" + "\n\nFor example, "
                        + "\n\t the normalized scientific notation format is \"#.0###E0##\""
                        + "\n\t to make the digits required \"0.00000E000\"\n\n";

                // Add custom HDFLarge icon to dialog
                String str = (new InputDialog(theShell, "Create a custom number format", msg)).open();

                if ((str == null) || (str.length() < 1)) {
                    return;
                }

                customFormat.applyPattern(str);
            }
        });

        int type = dataObject.getDatatype().getDatatypeClass();
        boolean isInt = (NT == 'B' || NT == 'S' || NT == 'I' || NT == 'J');

        if ((dataObject instanceof ScalarDS)
                && (isInt || type == Datatype.CLASS_BITFIELD || type == Datatype.CLASS_OPAQUE)) {
            checkHex = new MenuItem(menu, SWT.CHECK);
            checkHex.setText("Show Hexadecimal");
            checkHex.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    showAsHex = checkHex.getSelection();
                    if (showAsHex) {
                        if (checkScientificNotation != null) checkScientificNotation.setSelection(false);
                        if (checkCustomNotation != null) checkCustomNotation.setSelection(false);
                        if (checkBin != null) checkBin.setSelection(false);

                        showAsBin = false;
                        numberFormat = normalFormat;
                    }

                    dataTable.doCommand(new VisualRefreshCommand());
                }
            });

            checkBin = new MenuItem(menu, SWT.CHECK);
            checkBin.setText("Show Binary");
            checkBin.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    showAsBin = checkBin.getSelection();
                    if (showAsBin) {
                        if (checkScientificNotation != null) checkScientificNotation.setSelection(false);
                        if (checkCustomNotation != null) checkCustomNotation.setSelection(false);
                        if (checkHex != null) checkHex.setSelection(false);

                        showAsHex = false;
                        numberFormat = normalFormat;
                    }

                    dataTable.doCommand(new VisualRefreshCommand());
                }
            });
        }

        new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Close");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                theShell.dispose();
            }
        });

        return menuBar;
    }

    protected abstract NatTable createTable(Composite parent, DataFormat dataObject);

    protected abstract void updateValueInMemory(String cellValue, int row, int coll) throws Exception;

    protected abstract void showObjRefData(long ref);

    protected abstract void showRegRefData(String reg);

    protected abstract DisplayConverter getDataDisplayConverter(DataFormat dataObject);

    protected abstract DataValidator getDataValidator(DataFormat dataObject);

    protected abstract IEditableRule getDataEditingRule(DataFormat dataObject);

    @Override
    public HObject getDataObject() {
        return (HObject) dataObject;
    }

    @Override
    public Object getTable() {
        return dataTable;
    }

    @Override
    public int getSelectedRowCount() {
        return selectionLayer.getSelectedRowCount();
    }

    @Override
    public int getSelectedColumnCount() {
        return selectionLayer.getSelectedColumnPositions().length;
    }

    public SelectionLayer getSelectionLayer() {
        return selectionLayer;
    }

    public DataLayer getDataLayer() {
        return dataLayer;
    }

    // Flip to previous 'frame' of Table data
    private void previousFrame() {
        // Only valid operation if data object has 3 or more dimensions
        if (dataObject.getRank() < 3) return;

        long[] start = dataObject.getStartDims();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long curFrame = start[selectedIndex[2]];

        if (curFrame == 0) return; // Current frame is the first frame

        gotoFrame(curFrame - 1);
    }

    // Flip to next 'frame' of Table data
    private void nextFrame() {
        // Only valid operation if data object has 3 or more dimensions
        if (dataObject.getRank() < 3) return;

        long[] start = dataObject.getStartDims();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long[] dims = dataObject.getDims();
        long curFrame = start[selectedIndex[2]];

        if (curFrame == dims[selectedIndex[2]] - 1) return; // Current frame is the last frame

        gotoFrame(curFrame + 1);
    }

    // Flip to the first 'frame' of Table data
    private void firstFrame() {
        // Only valid operation if data object has 3 or more dimensions
        if (dataObject.getRank() < 3) return;

        long[] start = dataObject.getStartDims();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long curFrame = start[selectedIndex[2]];

        if (curFrame == 0) return; // Current frame is the first frame

        gotoFrame(0);
    }

    // Flip to the last 'frame' of Table data
    private void lastFrame() {
        // Only valid operation if data object has 3 or more dimensions
        if (dataObject.getRank() < 3) return;

        long[] start = dataObject.getStartDims();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long[] dims = dataObject.getDims();
        long curFrame = start[selectedIndex[2]];

        if (curFrame == dims[selectedIndex[2]] - 1) return; // Current page is the last page

        gotoFrame(dims[selectedIndex[2]] - 1);
    }

    // Flip to the specified 'frame' of Table data
    private void gotoFrame(long idx) {
        // Only valid operation if data object has 3 or more dimensions
        if (dataObject.getRank() < 3 || idx == (curDataFrame - indexBase)) {
            return;
        }

        // Make sure to save any changes to this frame of data before changing frames
        if (isValueChanged) {
            updateValueInFile();
        }

        long[] start = dataObject.getStartDims();
        int[] selectedIndex = dataObject.getSelectedIndex();
        long[] dims = dataObject.getDims();

        // Do a bit of frame index validation
        if ((idx < 0) || (idx >= dims[selectedIndex[2]])) {
            shell.getDisplay().beep();
            Tools.showError(shell,
                    "Frame number must be between " + indexBase + " and " + (dims[selectedIndex[2]] - 1 + indexBase),
                    shell.getText());
            return;
        }

        start[selectedIndex[2]] = idx;
        curDataFrame = idx + indexBase;
        frameField.setText(String.valueOf(curDataFrame));

        dataObject.clearData();

        shell.setCursor(display.getSystemCursor(SWT.CURSOR_WAIT));

        try {
            dataValue = dataObject.getData();
            if (dataObject instanceof ScalarDS) {
                ((ScalarDS) dataObject).convertFromUnsignedC();
                dataValue = dataObject.getData();
            }
        }
        catch (Exception ex) {
            dataValue = null;
            Tools.showError(shell, ex.getMessage(), shell.getText());
            return;
        }
        finally {
            shell.setCursor(null);
        }

        dataTable.doCommand(new VisualRefreshCommand());
    }

    /**
     * Copy data from the spreadsheet to the system clipboard.
     */
    private void copyData() {
        StringBuffer sb = new StringBuffer();

        Rectangle selection = selectionLayer.getLastSelectedRegion();
        if (selection == null) {
            Tools.showError(shell, "Select data to copy.", shell.getText());
            return;
        }

        int r0 = selectionLayer.getLastSelectedRegion().y; // starting row
        int c0 = selectionLayer.getLastSelectedRegion().x; // starting column

        if ((r0 < 0) || (c0 < 0)) {
            return;
        }

        int nr = selectionLayer.getSelectedRowCount();
        int nc = selectionLayer.getSelectedColumnPositions().length;
        int r1 = r0 + nr; // finish row
        int c1 = c0 + nc; // finishing column

        try {
            for (int i = r0; i < r1; i++) {
                sb.append(selectionLayer.getDataValueByPosition(c0, i).toString());
                for (int j = c0 + 1; j < c1; j++) {
                    sb.append("\t");
                    sb.append(selectionLayer.getDataValueByPosition(j, i).toString());
                }
                sb.append("\n");
            }
        }
        catch (java.lang.OutOfMemoryError err) {
            shell.getDisplay().beep();
            Tools.showError(shell,
                    "Copying data to system clipboard failed. \nUse \"export/import data\" for copying/pasting large data.",
                    shell.getText());
            return;
        }

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contents = new StringSelection(sb.toString());
        cb.setContents(contents, null);
    }

    /**
     * Paste data from the system clipboard to the spreadsheet.
     */
    private void pasteData() {
        if (!MessageDialog.openConfirm(shell, "Clipboard Data", "Do you want to paste selected data?")) return;

        int cols = selectionLayer.getPreferredColumnCount();
        int rows = selectionLayer.getPreferredRowCount();
        int r0 = 0;
        int c0 = 0;

        Rectangle selection = selectionLayer.getLastSelectedRegion();
        if (selection != null) {
            r0 = selection.y;
            c0 = selection.x;
        }

        if (c0 < 0) {
            c0 = 0;
        }
        if (r0 < 0) {
            r0 = 0;
        }
        int r = r0;
        int c = c0;

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        String line = "";
        try {
            String s = (String) cb.getData(DataFlavor.stringFlavor);

            StringTokenizer st = new StringTokenizer(s, "\n");
            // read line by line
            while (st.hasMoreTokens() && (r < rows)) {
                line = st.nextToken();

                if (fixedDataLength < 1) {
                    // separate by delimiter
                    StringTokenizer lt = new StringTokenizer(line, "\t");
                    while (lt.hasMoreTokens() && (c < cols)) {
                        try {
                            updateValueInMemory(lt.nextToken(), r, c);
                        }
                        catch (Exception ex) {
                            continue;
                        }
                        c++;
                    }
                    r = r + 1;
                    c = c0;
                }
                else {
                    // the data has fixed length
                    int n = line.length();
                    String theVal;
                    for (int i = 0; i < n; i = i + fixedDataLength) {
                        try {
                            theVal = line.substring(i, i + fixedDataLength);
                            updateValueInMemory(theVal, r, c);
                        }
                        catch (Exception ex) {
                            continue;
                        }
                        c++;
                    }
                }
            }
        }
        catch (Throwable ex) {
            shell.getDisplay().beep();
            Tools.showError(shell, ex.getMessage(), shell.getText());
        }
    }

    /**
     * Save data as text.
     *
     * @throws Exception
     *             if a failure occurred
     */
    private void saveAsText() throws Exception {
        String currentDir = ((HObject) dataObject).getFileFormat().getParent();

        String filename = null;
        if (((HDFView) viewer).getTestState()) {
            filename = currentDir + File.separator + new InputDialog(shell, "Enter a file name", "").open();
        }
        else {
            FileDialog fChooser = new FileDialog(shell, SWT.SAVE);
            fChooser.setFilterPath(currentDir);

            DefaultFileFilter filter = DefaultFileFilter.getFileFilterText();
            fChooser.setFilterExtensions(new String[] { "*.*", filter.getExtensions() });
            fChooser.setFilterNames(new String[] { "All Files", filter.getDescription() });
            fChooser.setFilterIndex(1);
            fChooser.setText("Save Current Data To Text File --- " + ((HObject) dataObject).getName());

            filename = fChooser.open();
        }
        if (filename == null) return;

        File chosenFile = new File(filename);
        String fname = chosenFile.getAbsolutePath();

        log.trace("saveAsText: file={}", fname);

        // Check if the file is in use and prompt for overwrite
        if (chosenFile.exists()) {
            List<?> fileList = viewer.getTreeView().getCurrentFiles();
            if (fileList != null) {
                FileFormat theFile = null;
                Iterator<?> iterator = fileList.iterator();
                while (iterator.hasNext()) {
                    theFile = (FileFormat) iterator.next();
                    if (theFile.getFilePath().equals(fname)) {
                        shell.getDisplay().beep();
                        Tools.showError(shell,
                                "Unable to save data to file \"" + fname + "\". \nThe file is being used.",
                                shell.getText());
                        return;
                    }
                }
            }

            if (!MessageDialog.openConfirm(shell, shell.getText(), "File exists. Do you want to replace it?")) return;
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(chosenFile)));

        String delName = ViewProperties.getDataDelimiter();
        String delimiter = "";

        // delimiter must include a tab to be consistent with copy/paste for
        // compound fields
        if (dataObject instanceof CompoundDS) delimiter = "\t";

        if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_TAB)) {
            delimiter = "\t";
        }
        else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_SPACE)) {
            delimiter = " " + delimiter;
        }
        else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_COMMA)) {
            delimiter = "," + delimiter;
        }
        else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_COLON)) {
            delimiter = ":" + delimiter;
        }
        else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_SEMI_COLON)) {
            delimiter = ";" + delimiter;
        }

        int cols = selectionLayer.getPreferredColumnCount();
        int rows = selectionLayer.getPreferredRowCount();

        for (int i = 0; i < rows; i++) {
            out.print(selectionLayer.getDataValueByPosition(0, i));
            for (int j = 1; j < cols; j++) {
                out.print(delimiter);
                out.print(selectionLayer.getDataValueByPosition(j, i));
            }
            out.println();
        }

        out.flush();
        out.close();

        viewer.showStatus("Data saved to: " + fname);
    }

    /** Save data as text (from TextView). */
    // private void saveAsTextTextView() throws Exception {
    // FileDialog fChooser = new FileDialog(shell, SWT.SAVE);
    // fChooser.setText("Save Current Data To Text File --- " + dataset.getName());
    // fChooser.setFilterPath(dataset.getFileFormat().getParent());
    //
    // DefaultFileFilter filter = DefaultFileFilter.getFileFilterText();
    // fChooser.setFilterExtensions(new String[] {"*.*", filter.getExtensions()});
    // fChooser.setFilterNames(new String[] {"All Files", filter.getDescription()});
    // fChooser.setFilterIndex(1);
    //
    // // fchooser.changeToParentDirectory();
    // fChooser.setFileName(dataset.getName() + ".txt");
    // fChooser.setOverwrite(true);
    //
    // String filename = fChooser.open();
    //
    // if (filename == null) return;
    //
    // File chosenFile = new File(filename);
    //
    // // check if the file is in use
    // String fname = chosenFile.getAbsolutePath();
    // List<FileFormat> fileList = viewer.getTreeView().getCurrentFiles();
    // if (fileList != null) {
    // FileFormat theFile = null;
    // Iterator<FileFormat> iterator = fileList.iterator();
    // while (iterator.hasNext()) {
    // theFile = iterator.next();
    // if (theFile.getFilePath().equals(fname)) {
    // Tools.showError(shell, "Unable to save data to file \"" + fname
    // + "\". \nThe file is being used.", shell.getText());
    // return;
    // }
    // }
    // }
    //
    // PrintWriter out = new PrintWriter(new BufferedWriter(new
    // FileWriter(chosenFile)));
    //
    // int rows = text.length;
    // for (int i = 0; i < rows; i++) {
    // out.print(text[i].trim());
    // out.println();
    // out.println();
    // }
    //
    // out.flush();
    // out.close();
    //
    // viewer.showStatus("Data save to: " + fname);
    //
    // try {
    // RandomAccessFile rf = new RandomAccessFile(chosenFile, "r");
    // long size = rf.length();
    // rf.close();
    // viewer.showStatus("File size (bytes): " + size);
    // }
    // catch (Exception ex) {
    // log.debug("raf file size:", ex);
    // }
    // }

    // print the table (from TextView)
    // private void print() {
    // // StreamPrintServiceFactory[] spsf = StreamPrintServiceFactory
    // // .lookupStreamPrintServiceFactories(null, null);
    // // for (int i = 0; i < spsf.length; i++) {
    // // System.out.println(spsf[i]);
    // // }
    // // DocFlavor[] docFlavors = spsf[0].getSupportedDocFlavors();
    // // for (int i = 0; i < docFlavors.length; i++) {
    // // System.out.println(docFlavors[i]);
    // // }
    //
    // // TODO: windows url
    // // Get a text DocFlavor
    // InputStream is = null;
    // try {
    // is = new BufferedInputStream(new java.io.FileInputStream(
    // "e:\\temp\\t.html"));
    // }
    // catch (Exception ex) {
    // log.debug("Get a text DocFlavor:", ex);
    // }
    // DocFlavor flavor = DocFlavor.STRING.TEXT_HTML;
    //
    // // Get all available print services
    // PrintService[] services = PrintServiceLookup.lookupPrintServices(null,
    // null);
    //
    // // Print it
    // try {
    // // Print this job on the first print server
    // DocPrintJob job = services[0].createPrintJob();
    // Doc doc = new SimpleDoc(is, flavor, null);
    //
    // job.print(doc, null);
    // }
    // catch (Exception ex) {
    // log.debug("print(): failure: ", ex);
    // }
    // }

    /**
     * Save data as binary.
     *
     * @throws Exception
     *             if a failure occurred
     */
    private void saveAsBinary() throws Exception {
        String currentDir = ((HObject) dataObject).getFileFormat().getParent();

        String filename = null;
        if (((HDFView) viewer).getTestState()) {
            filename = currentDir + File.separator + new InputDialog(shell, "Enter a file name", "").open();
        }
        else {
            FileDialog fChooser = new FileDialog(shell, SWT.SAVE);
            fChooser.setFilterPath(currentDir);

            DefaultFileFilter filter = DefaultFileFilter.getFileFilterBinary();
            fChooser.setFilterExtensions(new String[] { "*.*", filter.getExtensions() });
            fChooser.setFilterNames(new String[] { "All Files", filter.getDescription() });
            fChooser.setFilterIndex(1);
            fChooser.setText("Save Current Data To Binary File --- " + ((HObject) dataObject).getName());

            filename = fChooser.open();
        }
        if (filename == null) return;

        File chosenFile = new File(filename);
        String fname = chosenFile.getAbsolutePath();

        log.trace("saveAsBinary: file={}", fname);

        // Check if the file is in use and prompt for overwrite
        if (chosenFile.exists()) {
            List<?> fileList = viewer.getTreeView().getCurrentFiles();
            if (fileList != null) {
                FileFormat theFile = null;
                Iterator<?> iterator = fileList.iterator();
                while (iterator.hasNext()) {
                    theFile = (FileFormat) iterator.next();
                    if (theFile.getFilePath().equals(fname)) {
                        shell.getDisplay().beep();
                        Tools.showError(shell,
                                "Unable to save data to file \"" + fname + "\". \nThe file is being used.",
                                shell.getText());
                        return;
                    }
                }
            }

            if (!MessageDialog.openConfirm(shell, shell.getText(), "File exists. Do you want to replace it?")) return;
        }

        FileOutputStream outputFile = new FileOutputStream(chosenFile);
        DataOutputStream out = new DataOutputStream(outputFile);

        if (dataObject instanceof ScalarDS) {
            ((ScalarDS) dataObject).convertToUnsignedC();
            Object data = dataObject.getData();
            ByteOrder bo = ByteOrder.nativeOrder();

            if (binaryOrder == 1)
                bo = ByteOrder.nativeOrder();
            else if (binaryOrder == 2)
                bo = ByteOrder.LITTLE_ENDIAN;
            else if (binaryOrder == 3) bo = ByteOrder.BIG_ENDIAN;

            Tools.saveAsBinary(out, data, bo);

            viewer.showStatus("Data saved to: " + fname);
        }
        else
            viewer.showStatus("Data not saved - not a ScalarDS");
    }

    /**
     * Import data values from text file.
     *
     * @param fname
     *            the file to import text from
     */
    private void importTextData(String fname) {
        int cols = selectionLayer.getPreferredColumnCount();
        int rows = selectionLayer.getPreferredRowCount();
        int r0;
        int c0;

        Rectangle lastSelection = selectionLayer.getLastSelectedRegion();
        if (lastSelection != null) {
            r0 = lastSelection.y;
            c0 = lastSelection.x;

            if (c0 < 0) {
                c0 = 0;
            }
            if (r0 < 0) {
                r0 = 0;
            }
        }
        else {
            r0 = 0;
            c0 = 0;
        }

        // Start at the first column for compound datasets
        if (dataObject instanceof CompoundDS) c0 = 0;

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fname));
        }
        catch (FileNotFoundException ex) {
            log.debug("import data values from text file {}:", fname, ex);
            return;
        }

        String line = null;
        StringTokenizer tokenizer1 = null;

        try {
            line = in.readLine();
        }
        catch (IOException ex) {
            try {
                in.close();
            }
            catch (IOException ex2) {
                log.debug("close text file {}:", fname, ex2);
            }
            log.debug("read text file {}:", fname, ex);
            return;
        }

        String delName = ViewProperties.getDataDelimiter();
        String delimiter = "";

        // delimiter must include a tab to be consistent with copy/paste for
        // compound fields
        if (dataObject instanceof CompoundDS)
            delimiter = "\t";
        else {
            if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_TAB)) {
                delimiter = "\t";
            }
            else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_SPACE)) {
                delimiter = " " + delimiter;
            }
            else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_COMMA)) {
                delimiter = ",";
            }
            else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_COLON)) {
                delimiter = ":";
            }
            else if (delName.equalsIgnoreCase(ViewProperties.DELIMITER_SEMI_COLON)) {
                delimiter = ";";
            }
        }
        String token = null;
        int r = r0;
        int c = c0;
        while ((line != null) && (r < rows)) {
            if (fixedDataLength > 0) {
                // the data has fixed length
                int n = line.length();
                String theVal;
                for (int i = 0; i < n; i = i + fixedDataLength) {
                    try {
                        theVal = line.substring(i, i + fixedDataLength);
                        updateValueInMemory(theVal, r, c);
                    }
                    catch (Exception ex) {
                        continue;
                    }
                    c++;
                }
            }
            else {
                try {
                    tokenizer1 = new StringTokenizer(line, delimiter);
                    while (tokenizer1.hasMoreTokens() && (c < cols)) {
                        token = tokenizer1.nextToken();
                        if (dataObject instanceof ScalarDS) {
                            StringTokenizer tokenizer2 = new StringTokenizer(token);
                            while (tokenizer2.hasMoreTokens() && (c < cols)) {
                                updateValueInMemory(tokenizer2.nextToken(), r, c);
                                c++;
                            }
                        }
                        else {
                            updateValueInMemory(token, r, c);
                            c++;
                        }
                    } // while (tokenizer1.hasMoreTokens() && index < size)
                }
                catch (Exception ex) {
                    Tools.showError(shell, ex.getMessage(), shell.getText());

                    try {
                        in.close();
                    }
                    catch (IOException ex2) {
                        log.debug("close text file {}:", fname, ex2);
                    }
                    return;
                }
            }

            try {
                line = in.readLine();
            }
            catch (IOException ex) {
                log.debug("read text file {}:", fname, ex);
                line = null;
            }

            // Start at the first column for compound datasets
            if (dataObject instanceof CompoundDS) {
                c = 0;
            }
            else {
                c = c0;
            }

            r++;
        } // while ((line != null) && (r < rows))

        try {
            in.close();
        }
        catch (IOException ex) {
            log.debug("close text file {}:", fname, ex);
        }
    }

    /**
     * Import data values from binary file.
     */
    private void importBinaryData() {
        String currentDir = ((HObject) dataObject).getFileFormat().getParent();

        String filename = null;
        if (((HDFView) viewer).getTestState()) {
            filename = currentDir + File.separator + new InputDialog(shell, "Enter a file name", "").open();
        }
        else {
            FileDialog fChooser = new FileDialog(shell, SWT.OPEN);
            fChooser.setFilterPath(currentDir);

            DefaultFileFilter filter = DefaultFileFilter.getFileFilterBinary();
            fChooser.setFilterExtensions(new String[] { "*.*", filter.getExtensions() });
            fChooser.setFilterNames(new String[] { "All Files", filter.getDescription() });
            fChooser.setFilterIndex(1);

            filename = fChooser.open();
        }

        if (filename == null) return;

        File chosenFile = new File(filename);
        if (!chosenFile.exists()) {
            Tools.showError(shell, "File " + chosenFile.getName() + " does not exist.", "Import Data from Binary File");
            return;
        }

        if (!MessageDialog.openConfirm(shell, "Import Data", "Do you want to paste selected data?")) return;

        ByteOrder bo = ByteOrder.nativeOrder();
        if (binaryOrder == 1)
            bo = ByteOrder.nativeOrder();
        else if (binaryOrder == 2)
            bo = ByteOrder.LITTLE_ENDIAN;
        else if (binaryOrder == 2) bo = ByteOrder.BIG_ENDIAN;

        try {
            if (Tools.getBinaryDataFromFile(dataValue, chosenFile.getAbsolutePath(), bo)) isValueChanged = true;

            dataTable.doCommand(new StructuralRefreshCommand());
        }
        catch (Exception ex) {
            log.trace("importBinaryData(): {}", ex);
            return;
        }
        catch (OutOfMemoryError e) {
            log.trace("importBinaryData(): Out of memory");
            return;
        }
    }

    /**
     * Convert selected data based on predefined math functions.
     */
    private void mathConversion() throws Exception {
        log.trace("mathConversion(): start");

        if (isReadOnly) {
            log.debug("mathConversion(): can't convert read-only data");
            log.trace("mathConversion(): finish");
            return;
        }

        int cols = selectionLayer.getSelectedColumnPositions().length;
        if ((dataObject instanceof CompoundDS) && (cols > 1)) {
            shell.getDisplay().beep();
            Tools.showError(shell, "Please select one column at a time for math conversion" + "for compound dataset.",
                    shell.getText());
            log.debug("mathConversion(): more than one column selected for CompoundDS");
            log.trace("mathConversion(): finish");
            return;
        }

        Object theData = getSelectedData();
        if (theData == null) {
            shell.getDisplay().beep();
            Tools.showError(shell, "No data is selected.", shell.getText());
            log.debug("mathConversion(): no data selected");
            log.trace("mathConversion(): finish");
            return;
        }

        MathConversionDialog dialog = new MathConversionDialog(shell, theData);
        dialog.open();

        if (dialog.isConverted()) {
            if (dataObject instanceof CompoundDS) {
                Object colData = null;
                try {
                    colData = ((List<?>) dataObject.getData()).get(selectionLayer.getSelectedColumnPositions()[0]);
                }
                catch (Exception ex) {
                    log.debug("mathConversion(): ", ex);
                }

                if (colData != null) {
                    int size = Array.getLength(theData);
                    System.arraycopy(theData, 0, colData, 0, size);
                }
            }
            else {
                int rows = selectionLayer.getSelectedRowCount();

                // Since NatTable returns the selected row positions as a Set<Range>, convert
                // this to
                // an Integer[]
                Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
                Set<Integer> selectedRowPos = new LinkedHashSet<>();
                Iterator<Range> i1 = rowPositions.iterator();
                while (i1.hasNext()) {
                    selectedRowPos.addAll(i1.next().getMembers());
                }

                int r0 = selectedRowPos.toArray(new Integer[0])[0];
                int c0 = selectionLayer.getSelectedColumnPositions()[0];

                int w = dataTable.getPreferredColumnCount() - 1;
                int idx_src = 0;
                int idx_dst = 0;

                for (int i = 0; i < rows; i++) {
                    idx_dst = (r0 + i) * w + c0;
                    System.arraycopy(theData, idx_src, dataValue, idx_dst, cols);
                    idx_src += cols;
                }
            }

            theData = null;
            System.gc();
            isValueChanged = true;

            log.trace("mathConversion(): finish");
        }
    }

    private void showLineplot() {
        // Since NatTable returns the selected row positions as a Set<Range>, convert
        // this to
        // an Integer[]
        Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
        Set<Integer> selectedRowPos = new LinkedHashSet<>();
        Iterator<Range> i1 = rowPositions.iterator();
        while (i1.hasNext()) {
            selectedRowPos.addAll(i1.next().getMembers());
        }

        Integer[] rows = selectedRowPos.toArray(new Integer[0]);
        int[] cols = selectionLayer.getSelectedColumnPositions();

        if ((rows == null) || (cols == null) || (rows.length <= 0) || (cols.length <= 0)) {
            shell.getDisplay().beep();
            Tools.showError(shell, "Select rows/columns to draw line plot.", shell.getText());
            return;
        }

        int nrow = dataTable.getPreferredRowCount() - 1;
        int ncol = dataTable.getPreferredColumnCount() - 1;

        log.trace("DefaultTableView showLineplot: {} - {}", nrow, ncol);
        LinePlotOption lpo = new LinePlotOption(shell, SWT.NONE, nrow, ncol);
        lpo.open();

        int plotType = lpo.getPlotBy();
        if (plotType == LinePlotOption.NO_PLOT) {
            return;
        }

        boolean isRowPlot = (plotType == LinePlotOption.ROW_PLOT);
        int xIndex = lpo.getXindex();

        // figure out to plot data by row or by column
        // Plot data by rows if all columns are selected and part of
        // rows are selected, otherwise plot data by column
        double[][] data = null;
        int nLines = 0;
        String title = "Lineplot - " + ((HObject) dataObject).getPath() + ((HObject) dataObject).getName();
        String[] lineLabels = null;
        double[] yRange = { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
        double xData[] = null;

        if (isRowPlot) {
            title += " - by row";
            nLines = rows.length;
            if (nLines > 10) {
                shell.getDisplay().beep();
                nLines = 10;
                MessageDialog.openWarning(shell, shell.getText(),
                        "More than 10 rows are selected.\n" + "The first 10 rows will be displayed.");
            }
            lineLabels = new String[nLines];
            data = new double[nLines][cols.length];

            double value = 0.0;
            for (int i = 0; i < nLines; i++) {
                lineLabels[i] = String.valueOf(rows[i] + indexBase);
                for (int j = 0; j < cols.length; j++) {
                    data[i][j] = 0;
                    try {
                        value = Double.parseDouble(selectionLayer.getDataValueByPosition(cols[j], rows[i]).toString());
                        data[i][j] = value;
                        yRange[0] = Math.min(yRange[0], value);
                        yRange[1] = Math.max(yRange[1], value);
                    }
                    catch (NumberFormatException ex) {
                        log.debug("rows[{}]:", i, ex);
                    }
                } // for (int j = 0; j < ncols; j++)
            } // for (int i = 0; i < rows.length; i++)

            if (xIndex >= 0) {
                xData = new double[cols.length];
                for (int j = 0; j < cols.length; j++) {
                    xData[j] = 0;
                    try {
                        value = Double.parseDouble(selectionLayer.getDataValueByPosition(cols[j], xIndex).toString());
                        xData[j] = value;
                    }
                    catch (NumberFormatException ex) {
                        log.debug("xIndex of {}:", xIndex, ex);
                    }
                }
            }
        } // if (isRowPlot)
        else {
            title += " - by column";
            nLines = cols.length;
            if (nLines > 10) {
                shell.getDisplay().beep();
                nLines = 10;
                MessageDialog.openWarning(shell, shell.getText(),
                        "More than 10 columns are selected.\n" + "The first 10 columns will be displayed.");
            }
            lineLabels = new String[nLines];
            data = new double[nLines][rows.length];
            double value = 0.0;
            for (int j = 0; j < nLines; j++) {
                lineLabels[j] = columnHeaderDataProvider.getDataValue(cols[j] + indexBase, 0).toString();
                for (int i = 0; i < rows.length; i++) {
                    data[j][i] = 0;
                    try {
                        value = Double.parseDouble(selectionLayer.getDataValueByPosition(cols[j], rows[i]).toString());
                        data[j][i] = value;
                        yRange[0] = Math.min(yRange[0], value);
                        yRange[1] = Math.max(yRange[1], value);
                    }
                    catch (NumberFormatException ex) {
                        log.debug("cols[{}]:", j, ex);
                    }
                } // for (int j=0; j<ncols; j++)
            } // for (int i=0; i<rows.length; i++)

            if (xIndex >= 0) {
                xData = new double[rows.length];
                for (int j = 0; j < rows.length; j++) {
                    xData[j] = 0;
                    try {
                        value = Double.parseDouble(selectionLayer.getDataValueByPosition(xIndex, rows[j]).toString());
                        xData[j] = value;
                    }
                    catch (NumberFormatException ex) {
                        log.debug("xIndex of {}:", xIndex, ex);
                    }
                }
            }
        } // else

        int n = removeInvalidPlotData(data, xData, yRange);
        if (n < data[0].length) {
            double[][] dataNew = new double[data.length][n];
            for (int i = 0; i < data.length; i++)
                System.arraycopy(data[i], 0, dataNew[i], 0, n);

            data = dataNew;

            if (xData != null) {
                double[] xDataNew = new double[n];
                System.arraycopy(xData, 0, xDataNew, 0, n);
                xData = xDataNew;
            }
        }

        // allow to draw a flat line: all values are the same
        if (yRange[0] == yRange[1]) {
            yRange[1] += 1;
            yRange[0] -= 1;
        }
        else if (yRange[0] > yRange[1]) {
            shell.getDisplay().beep();
            Tools.showError(shell, "Cannot show line plot for the selected data. \n" + "Please check the data range: ("
                    + yRange[0] + ", " + yRange[1] + ").", shell.getText());
            data = null;
            return;
        }
        if (xData == null) { // use array index and length for x data range
            xData = new double[2];
            xData[0] = indexBase; // 1- or zero-based
            xData[1] = data[0].length + indexBase - 1; // maximum index
        }

        Chart cv = new Chart(shell, title, Chart.LINEPLOT, data, xData, yRange);
        cv.setLineLabels(lineLabels);

        String cname = dataValue.getClass().getName();
        char dname = cname.charAt(cname.lastIndexOf("[") + 1);
        if ((dname == 'B') || (dname == 'S') || (dname == 'I') || (dname == 'J')) {
            cv.setTypeToInteger();
        }

        cv.open();
    }

    /**
     * Remove values of NaN, INF from the array.
     *
     * @param data
     *            the data array
     * @param xData
     *            the x-axis data points
     * @param yRange
     *            the range of data values
     *
     * @return number of data points in the plot data if successful; otherwise,
     *         returns false.
     */
    private int removeInvalidPlotData(double[][] data, double[] xData, double[] yRange) {
        int idx = 0;
        boolean hasInvalid = false;

        if (data == null || yRange == null) return -1;

        yRange[0] = Double.POSITIVE_INFINITY;
        yRange[1] = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < data[0].length; i++) {
            hasInvalid = false;

            for (int j = 0; j < data.length; j++) {
                hasInvalid = Tools.isNaNINF(data[j][i]);
                if (xData != null) hasInvalid = hasInvalid || Tools.isNaNINF(xData[i]);

                if (hasInvalid)
                    break;
                else {
                    data[j][idx] = data[j][i];
                    if (xData != null) xData[idx] = xData[i];
                    yRange[0] = Math.min(yRange[0], data[j][idx]);
                    yRange[1] = Math.max(yRange[1], data[j][idx]);
                }
            }

            if (!hasInvalid) idx++;
        }

        return idx;
    }

    /**
     * An implementation of a GridLayer with support for column grouping and with
     * editing triggered by a double click instead of a single click.
     */
    protected class EditingGridLayer extends GridLayer {
        public EditingGridLayer(ILayer bodyLayer, ILayer columnHeaderLayer, ILayer rowHeaderLayer, ILayer cornerLayer) {
            super(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, false);

            // Left-align cells, change font for rendering cell text
            // and add cell data display converter for displaying as
            // Hexadecimal, Binary, etc.
            this.addConfiguration(new AbstractRegistryConfiguration() {
                @Override
                public void configureRegistry(IConfigRegistry configRegistry) {
                    Style cellStyle = new Style();

                    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
                    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
                            Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

                    if (curFont != null) {
                        cellStyle.setAttributeValue(CellStyleAttributes.FONT, curFont);
                    }
                    else {
                        cellStyle.setAttributeValue(CellStyleAttributes.FONT, Display.getDefault().getSystemFont());
                    }

                    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
                            DisplayMode.NORMAL, GridRegion.BODY);

                    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
                            DisplayMode.SELECT, GridRegion.BODY);

                    // Add data display conversion capability
                    dataDisplayConverter = getDataDisplayConverter(dataObject);
                    if (dataDisplayConverter != null) {
                        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                                dataDisplayConverter, DisplayMode.NORMAL, GridRegion.BODY);
                    }
                }
            });

            if (isRegRef || isObjRef) {
                // Show data pointed to by reference on double click
                this.addConfiguration(new AbstractUiBindingConfiguration() {
                    @Override
                    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
                        uiBindingRegistry.registerDoubleClickBinding(new MouseEventMatcher(), new IMouseAction() {
                            @Override
                            public void run(NatTable table, MouseEvent event) {
                                if (!(isRegRef || isObjRef)) return;

                                viewType = ViewType.TABLE;

                                Object theData = null;
                                try {
                                    theData = ((Dataset) getDataObject()).getData();
                                }
                                catch (Exception ex) {
                                    log.debug("show reference data: ", ex);
                                    theData = null;
                                    Tools.showError(shell, ex.getMessage(), shell.getText());
                                }

                                if (theData == null) {
                                    shell.getDisplay().beep();
                                    Tools.showError(shell, "No data selected.", shell.getText());
                                    return;
                                }

                                // Since NatTable returns the selected row positions as a Set<Range>, convert
                                // this to an Integer[]
                                Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
                                Set<Integer> selectedRowPos = new LinkedHashSet<>();
                                Iterator<Range> i1 = rowPositions.iterator();
                                while (i1.hasNext()) {
                                    selectedRowPos.addAll(i1.next().getMembers());
                                }

                                Integer[] selectedRows = selectedRowPos.toArray(new Integer[0]);
                                if (selectedRows == null || selectedRows.length <= 0) {
                                    log.debug("show reference data: no data selected");
                                    Tools.showError(shell, "No data selected.", shell.getText());
                                    return;
                                }
                                int len = Array.getLength(selectedRows);
                                for (int i = 0; i < len; i++) {
                                    if (isRegRef)
                                        showRegRefData((String) Array.get(theData, selectedRows[i]));
                                    else if (isObjRef) showObjRefData(Array.getLong(theData, selectedRows[i]));
                                }
                            }
                        });
                    }
                });
            }
            else {
                // Add default bindings for editing
                this.addConfiguration(new DefaultEditConfiguration());

                // Register cell editing rules with the table and add
                // data validation
                this.addConfiguration(new AbstractRegistryConfiguration() {
                    @Override
                    public void configureRegistry(IConfigRegistry configRegistry) {
                        IEditableRule editingRule = getDataEditingRule(dataObject);
                        if (editingRule != null) {
                            // Register cell editing rules with table
                            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                                    editingRule, DisplayMode.EDIT);
                        }

                        // Add data validator and validation error handler
                        DataValidator validator = getDataValidator(dataObject);
                        if (validator != null) {
                            configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, validator,
                                    DisplayMode.EDIT, GridRegion.BODY);
                        }

                        configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
                                new DialogErrorHandling(), DisplayMode.EDIT, GridRegion.BODY);
                    }
                });

                // Change cell editing to be on double click rather than single click
                // and allow editing of cells by pressing keys as well
                this.addConfiguration(new AbstractUiBindingConfiguration() {
                    @Override
                    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
                        uiBindingRegistry.registerFirstDoubleClickBinding(
                                new BodyCellEditorMouseEventMatcher(TextCellEditor.class), new MouseEditAction());

                        uiBindingRegistry.registerFirstKeyBinding(new LetterOrDigitKeyEventMatcher(),
                                new KeyEditAction());
                    }
                });
            }
        }
    }

    /**
     * An implementation of the table's Row Header which adapts to the current font.
     */
    protected class RowHeader extends RowHeaderLayer {
        public RowHeader(IUniqueIndexLayer baseLayer, ILayer verticalLayerDependency, SelectionLayer selectionLayer) {
            super(baseLayer, verticalLayerDependency, selectionLayer);

            this.addConfiguration(new DefaultRowHeaderLayerConfiguration() {
                @Override
                public void addRowHeaderStyleConfig() {
                    this.addConfiguration(new DefaultRowHeaderStyleConfiguration() {
                        {
                            this.cellPainter = new LineBorderDecorator(new TextPainter(false, true, 2, true));
                            this.bgColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
                            this.font = (curFont == null) ? Display.getDefault().getSystemFont() : curFont;
                        }
                    });
                }
            });
        }
    }

    /**
     * Custom Row Header data provider to set row indices based on Index Base for
     * both Scalar Datasets and Compound Datasets.
     */
    protected class RowHeaderDataProvider implements IDataProvider {

        private final int    rank;
        private final long[] dims;
        private final long[] startArray;
        private final long[] strideArray;
        private final int[]  selectedIndex;

        protected final int  start;
        protected final int  stride;

        private final int    nrows;

        public RowHeaderDataProvider(DataFormat theDataObject) {
            this.rank = theDataObject.getRank();
            this.dims = theDataObject.getSelectedDims();
            this.startArray = theDataObject.getStartDims();
            this.strideArray = theDataObject.getStride();
            this.selectedIndex = theDataObject.getSelectedIndex();

            if (rank > 1) {
                this.nrows = (int) theDataObject.getHeight();
            }
            else {
                this.nrows = (int) dims[0];
            }

            start = (int) startArray[selectedIndex[0]];
            stride = (int) strideArray[selectedIndex[0]];
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return nrows;
        }

        @Override
        public Object getDataValue(int columnIndex, int rowIndex) {
            return String.valueOf(start + indexBase + (rowIndex * stride));
        }

        @Override
        public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
            return;
        }
    }

    /**
     * An implementation of the table's Column Header which adapts to the current
     * font.
     */
    protected class ColumnHeader extends ColumnHeaderLayer {
        public ColumnHeader(IUniqueIndexLayer baseLayer, ILayer horizontalLayerDependency,
                SelectionLayer selectionLayer) {
            super(baseLayer, horizontalLayerDependency, selectionLayer);

            this.addConfiguration(new DefaultColumnHeaderLayerConfiguration() {
                @Override
                public void addColumnHeaderStyleConfig() {
                    this.addConfiguration(new DefaultColumnHeaderStyleConfiguration() {
                        {
                            this.cellPainter = new BeveledBorderDecorator(new TextPainter(false, true, 2, true));
                            this.bgColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
                            this.font = (curFont == null) ? Display.getDefault().getSystemFont() : curFont;
                        }
                    });
                }
            });
        }
    }

    // Context-menu for dealing with region and object references
    protected class RefContextMenu extends AbstractUiBindingConfiguration {
        private final Menu contextMenu;

        public RefContextMenu(NatTable table) {
            this.contextMenu = createMenu(table).build();
        }

        private PopupMenuBuilder createMenu(NatTable table) {
            Menu menu = new Menu(table);

            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText("Show As &Table");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    viewType = ViewType.TABLE;

                    log.trace("show reference data: Show data as {}", viewType);

                    Object theData = getSelectedData();
                    if (theData == null) {
                        shell.getDisplay().beep();
                        Tools.showError(shell, "No data selected.", shell.getText());
                        return;
                    }

                    // Since NatTable returns the selected row positions as a Set<Range>, convert
                    // this to an Integer[]
                    Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
                    Set<Integer> selectedRowPos = new LinkedHashSet<>();
                    Iterator<Range> i1 = rowPositions.iterator();
                    while (i1.hasNext()) {
                        selectedRowPos.addAll(i1.next().getMembers());
                    }

                    Integer[] selectedRows = selectedRowPos.toArray(new Integer[0]);
                    int[] selectedCols = selectionLayer.getSelectedColumnPositions();
                    if (selectedRows == null || selectedRows.length <= 0) {
                        shell.getDisplay().beep();
                        Tools.showError(shell, "No data selected.", shell.getText());
                        log.trace("show reference data: Show data as {}: selectedRows is empty", viewType);
                        return;
                    }

                    int len = Array.getLength(selectedRows) * Array.getLength(selectedCols);
                    log.trace("show reference data: Show data as {}: len={}", viewType, len);

                    for (int i = 0; i < len; i++) {
                        if (isRegRef) {
                            log.trace("show reference data: Show data[{}] as {}: isRegRef={}", i, viewType, isRegRef);
                            showRegRefData((String) Array.get(theData, i));
                        }
                        else if (isObjRef) {
                            log.trace("show reference data: Show data[{}] as {}: isObjRef={}", i, viewType, isObjRef);
                            showObjRefData(Array.getLong(theData, i));
                        }
                    }
                }
            });

            item = new MenuItem(menu, SWT.PUSH);
            item.setText("Show As &Image");
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    viewType = ViewType.IMAGE;

                    log.trace("show reference data: Show data as {}: ", viewType);

                    Object theData = getSelectedData();
                    if (theData == null) {
                        shell.getDisplay().beep();
                        Tools.showError(shell, "No data selected.", shell.getText());
                        return;
                    }

                    // Since NatTable returns the selected row positions as a Set<Range>, convert
                    // this to an Integer[]
                    Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
                    Set<Integer> selectedRowPos = new LinkedHashSet<>();
                    Iterator<Range> i1 = rowPositions.iterator();
                    while (i1.hasNext()) {
                        selectedRowPos.addAll(i1.next().getMembers());
                    }

                    Integer[] selectedRows = selectedRowPos.toArray(new Integer[0]);
                    int[] selectedCols = selectionLayer.getSelectedColumnPositions();
                    if (selectedRows == null || selectedRows.length <= 0) {
                        shell.getDisplay().beep();
                        Tools.showError(shell, "No data selected.", shell.getText());
                        log.trace("show reference data: Show data as {}: selectedRows is empty", viewType);
                        return;
                    }

                    int len = Array.getLength(selectedRows) * Array.getLength(selectedCols);
                    log.trace("show reference data: Show data as {}: len={}", viewType, len);

                    for (int i = 0; i < len; i++) {
                        if (isRegRef) {
                            log.trace("show reference data: Show data[{}] as {}: isRegRef={}", i, viewType, isRegRef);
                            showRegRefData((String) Array.get(theData, i));
                        }
                        else if (isObjRef) {
                            log.trace("show reference data: Show data[{}] as {}: isObjRef={}", i, viewType, isObjRef);
                            showObjRefData(Array.getLong(theData, i));
                        }
                    }
                }
            });

            // item = new MenuItem(menu, SWT.PUSH);
            // item.setText("Show As &Text");
            // item.addSelectionListener(new SelectionAdapter() {
            // public void widgetSelected(SelectionEvent e) {
            // viewType = ViewType.IMAGE;
            //
            // log.trace("show reference data: Show data as {}: ", viewType);
            //
            // Object theData = getSelectedData();
            // if (theData == null) {
            // shell.getDisplay().beep();
            // Tools.showError(shell, "No data selected.", shell.getText());
            // return;
            // }
            //
            // // Since NatTable returns the selected row positions as a Set<Range>, convert
            // this to
            // // an Integer[]
            // Set<Range> rowPositions = selectionLayer.getSelectedRowPositions();
            // Set<Integer> selectedRowPos = new LinkedHashSet<Integer>();
            // Iterator<Range> i1 = rowPositions.iterator();
            // while(i1.hasNext()) {
            // selectedRowPos.addAll(i1.next().getMembers());
            // }
            //
            // Integer[] selectedRows = selectedRowPos.toArray(new Integer[0]);
            // int[] selectedCols = selectionLayer.getFullySelectedColumnPositions();
            // if (selectedRows == null || selectedRows.length <= 0) {
            // log.trace("show reference data: Show data as {}: selectedRows is empty",
            // viewType);
            // return;
            // }
            //
            // int len = Array.getLength(selectedRows) * Array.getLength(selectedCols);
            // log.trace("show reference data: Show data as {}: len={}", viewType, len);
            //
            // for (int i = 0; i < len; i++) {
            // if (isRegRef) {
            // log.trace("show reference data: Show data[{}] as {}: isRegRef={}", i,
            // viewType, isRegRef);
            // showRegRefData((String) Array.get(theData, i));
            // }
            // else if (isObjRef) {
            // log.trace("show reference data: Show data[{}] as {}: isObjRef={}", i,
            // viewType, isObjRef);
            // showObjRefData(Array.getLong(theData, i));
            // }
            // }
            // }
            // });

            return new PopupMenuBuilder(table, menu);
        }

        @Override
        public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
            uiBindingRegistry.registerMouseDownBinding(
                    new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON),
                    new PopupMenuAction(this.contextMenu));
        }
    }

    private class LinePlotOption extends Dialog {

        private Shell linePlotOptionShell;

        private Button rowButton, colButton;

        private Combo rowBox, colBox;

        public static final int NO_PLOT = -1;
        public static final int ROW_PLOT = 0;
        public static final int COLUMN_PLOT = 1;

        private int nrow, ncol;

        private int idx_xaxis = -1, plotType = -1;

        public LinePlotOption(Shell parent, int style, int nrow, int ncol) {
            super(parent, style);

            this.nrow = nrow;
            this.ncol = ncol;
        }

        public void open() {
            Shell parent = getParent();
            linePlotOptionShell = new Shell(parent, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
            linePlotOptionShell.setFont(curFont);
            linePlotOptionShell.setText("Line Plot Options -- " + ((HObject) dataObject).getName());
            linePlotOptionShell.setImage(ViewProperties.getHdfIcon());
            linePlotOptionShell.setLayout(new GridLayout(1, true));

            Label label = new Label(linePlotOptionShell, SWT.RIGHT);
            label.setFont(curFont);
            label.setText("Select Line Plot Options:");

            Composite content = new Composite(linePlotOptionShell, SWT.BORDER);
            content.setLayout(new GridLayout(3, false));
            content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            label = new Label(content, SWT.RIGHT);
            label.setFont(curFont);
            label.setText(" Series in:");

            colButton = new Button(content, SWT.RADIO);
            colButton.setFont(curFont);
            colButton.setText("Column");
            colButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
            colButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    colBox.setEnabled(true);
                    rowBox.setEnabled(false);
                }
            });

            rowButton = new Button(content, SWT.RADIO);
            rowButton.setFont(curFont);
            rowButton.setText("Row");
            rowButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
            rowButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    rowBox.setEnabled(true);
                    colBox.setEnabled(false);
                }
            });

            label = new Label(content, SWT.RIGHT);
            label.setFont(curFont);
            label.setText(" For abscissa use:");

            long[] startArray = dataObject.getStartDims();
            long[] strideArray = dataObject.getStride();
            int[] selectedIndex = dataObject.getSelectedIndex();
            int start = (int) startArray[selectedIndex[0]];
            int stride = (int) strideArray[selectedIndex[0]];

            colBox = new Combo(content, SWT.SINGLE | SWT.READ_ONLY);
            colBox.setFont(curFont);
            GridData colBoxData = new GridData(SWT.FILL, SWT.FILL, true, false);
            colBoxData.minimumWidth = 100;
            colBox.setLayoutData(colBoxData);

            colBox.add("array index");

            for (int i = 0; i < ncol; i++) {
                colBox.add("column " + columnHeaderDataProvider.getDataValue(i, 0));
            }

            rowBox = new Combo(content, SWT.SINGLE | SWT.READ_ONLY);
            rowBox.setFont(curFont);
            GridData rowBoxData = new GridData(SWT.FILL, SWT.FILL, true, false);
            rowBoxData.minimumWidth = 100;
            rowBox.setLayoutData(rowBoxData);

            rowBox.add("array index");

            for (int i = 0; i < nrow; i++) {
                rowBox.add("row " + (start + indexBase + i * stride));
            }

            // Create Ok/Cancel button region
            Composite buttonComposite = new Composite(linePlotOptionShell, SWT.NONE);
            buttonComposite.setLayout(new GridLayout(2, true));
            buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

            Button okButton = new Button(buttonComposite, SWT.PUSH);
            okButton.setFont(curFont);
            okButton.setText("   &OK   ");
            okButton.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
            okButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (colButton.getSelection()) {
                        idx_xaxis = colBox.getSelectionIndex() - 1;
                        plotType = COLUMN_PLOT;
                    }
                    else {
                        idx_xaxis = rowBox.getSelectionIndex() - 1;
                        plotType = ROW_PLOT;
                    }

                    linePlotOptionShell.dispose();
                }
            });

            Button cancelButton = new Button(buttonComposite, SWT.PUSH);
            cancelButton.setFont(curFont);
            cancelButton.setText(" &Cancel ");
            cancelButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
            cancelButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    plotType = NO_PLOT;
                    linePlotOptionShell.dispose();
                }
            });

            colButton.setSelection(true);
            rowButton.setSelection(false);

            colBox.select(0);
            rowBox.select(0);

            colBox.setEnabled(colButton.getSelection());
            rowBox.setEnabled(rowButton.getSelection());

            linePlotOptionShell.pack();

            linePlotOptionShell.setMinimumSize(linePlotOptionShell.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            Rectangle parentBounds = parent.getBounds();
            Point shellSize = linePlotOptionShell.getSize();
            linePlotOptionShell.setLocation((parentBounds.x + (parentBounds.width / 2)) - (shellSize.x / 2),
                    (parentBounds.y + (parentBounds.height / 2)) - (shellSize.y / 2));

            linePlotOptionShell.open();

            Display display = parent.getDisplay();
            while (!linePlotOptionShell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
            }
        }

        int getXindex() {
            return idx_xaxis;
        }

        int getPlotBy() {
            return plotType;
        }
    }
}