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

package hdf.view.PaletteView;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import hdf.object.HObject;
import hdf.view.DataViewFactory;
import hdf.view.Tools;
import hdf.view.ViewManager;
import hdf.view.ViewProperties;
import hdf.view.ImageView.ImageView;
import hdf.view.MetaDataView.MetaDataView;
import hdf.view.TableView.TableView;

/**
 * A Factory class to return instances of classes implementing the PaletteView
 * interface, depending on the "current selected" PaletteView class in the list
 * maintained by the ViewProperties class.
 *
 * @author jhenderson
 * @version 1.0 4/18/2018
 */
public class PaletteViewFactory extends DataViewFactory {

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaletteViewFactory.class);

    @SuppressWarnings("rawtypes")
    @Override
    public TableView getTableView(ViewManager viewer, HashMap dataPropertiesMap) {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ImageView getImageView(ViewManager viewer, HashMap dataPropertiesMap) {
        return null;
    }

    @Override
    public PaletteView getPaletteView(Shell parent, ViewManager viewer, ImageView theImageView) {
        String dataViewName = ViewProperties.getPaletteViewList().get(0);
        Object[] initargs;
        PaletteView theView = null;

        log.trace("getPaletteView(): start");

        /* Retrieve the "currently selected" PaletteView class to use */
        List<?> paletteViewList = ViewProperties.getPaletteViewList();
        if ((paletteViewList == null) || (paletteViewList.size() <= 0)) {
            return null;
        }

        dataViewName = (String) paletteViewList.get(0);

        /* Attempt to load the class by name */
        Class<?> theClass = null;
        try {
            log.trace("getPaletteView(): Class.forName({})", dataViewName);

            /* Attempt to load the class by the given name */
            theClass = Class.forName(dataViewName);
        }
        catch (Exception ex) {
            log.debug("getPaletteView(): Class.forName({}) failure:", dataViewName, ex);

            try {
                log.trace("getPaletteView(): ViewProperties.loadExtClass().loadClass({})",
                        dataViewName);

                /* Attempt to load the class as an external module */
                theClass = ViewProperties.loadExtClass().loadClass(dataViewName);
            }
            catch (Exception ex2) {
                log.debug(
                        "getPaletteView(): ViewProperties.loadExtClass().loadClass({}) failure:",
                        dataViewName, ex);

                /* No loadable class found; use the default PaletteView */
                dataViewName = ViewProperties.DEFAULT_PALETTEVIEW_NAME;

                try {
                    log.trace("getPaletteView(): Class.forName({})", dataViewName);

                    theClass = Class.forName(dataViewName);
                }
                catch (Exception ex3) {
                    log.debug("getPaletteView(): Class.forName({}) failure:", dataViewName, ex);

                    theClass = null;
                }
            }
        }

        try {
            if (ViewProperties.DEFAULT_PALETTEVIEW_NAME.equals(dataViewName)) {
                initargs = new Object[] { parent, viewer, theImageView };
            }
            else {
                initargs = new Object[] { parent, theImageView };
            }

            theView = (PaletteView) Tools.newInstance(theClass, initargs);

            log.trace("getPaletteView(): returning PaletteView instance {}", theView);
        }
        catch (Exception ex) {
            log.debug("getPaletteView(): Error instantiating class:", ex);
        }

        log.trace("getPaletteView(): finish");

        return theView;
    }

    @Override
    public MetaDataView getMetaDataView(Composite parentObj, ViewManager viewer, HObject theObj) {
        return null;
    }

}