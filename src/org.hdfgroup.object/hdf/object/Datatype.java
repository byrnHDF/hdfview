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

package hdf.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Datatype is an abstract class that defines datatype characteristics and APIs for a data type.
 *
 * A datatype has four basic characteristics: class, size, byte order and sign. These characteristics are
 * defined in the See <a
 * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
 * Datatypes in HDF5 User Guide</a>
 *
 * These characteristics apply to all the sub-classes. The sub-classes may have different ways to describe a
 * datatype. We here define the <strong> native datatype</strong> to the datatype used by the sub-class. For
 * example, H5Datatype uses a datatype identifier (hid_t) to specify a datatype. NC2Datatype uses
 * ucar.nc2.DataType object to describe its datatype. "Native" here is different from the "native" definition
 * in the HDF5 library.
 *
 * Two functions, createNative() and fromNative(), are defined to convert the general characteristics to/from
 * the native datatype. Sub-classes must implement these functions so that the conversion will be done
 * correctly. The values of the CLASS member are not identical to HDF5 values for a datatype class.
 *
 * @version 1.1 9/4/2007
 * @author Peter X. Cao
 */
public abstract class Datatype extends HObject implements MetaDataContainer {
    private static final long serialVersionUID = -581324710549963177L;

    private static final Logger log = LoggerFactory.getLogger(Datatype.class);

    /**
     * The default definition for datatype size, order, and sign.
     */
    public static final int NATIVE = -1;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_NO_CLASS = -1;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_INTEGER = 0;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_FLOAT = 1;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_CHAR = 2;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_STRING = 3;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_BITFIELD = 4;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_OPAQUE = 5;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_COMPOUND = 6;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_REFERENCE = 7;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_ENUM = 8;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_VLEN = 9;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_ARRAY = 10;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_TIME = 11;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int CLASS_COMPLEX = 12;

    /**
     * See <a href=
     * "https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int ORDER_LE = 0;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int ORDER_BE = 1;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int ORDER_VAX = 2;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int ORDER_NONE = 3;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int SIGN_NONE = 0;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int SIGN_2 = 1;

    /**
     * See <a
     * href="https://support.hdfgroup.org/releases/hdf5/v1_14/v1_14_5/documentation/doxygen/_h5_t__u_g.html#sec_datatype">HDF5
     * Datatypes in HDF5 User Guide</a>
     */
    public static final int NSGN = 2;

    /**
     * The description of the datatype.
     */
    protected String datatypeDescription = null;

    /**
     * The description of the datatype.
     */
    protected boolean datatypeNATIVE = false;

    /**
     * The class of the datatype.
     */
    protected int datatypeClass;

    /**
     * The size (in bytes) of the datatype.
     */
    protected long datatypeSize;

    /**
     * The byte order of the datatype. Valid values are ORDER_LE, ORDER_BE, and
     * ORDER_VAX.
     */
    protected int datatypeOrder;

    /**
     * The sign of the datatype.
     */
    protected int datatypeSign;

    /**
     * The base datatype of this datatype (null if this datatype is atomic).
     */
    protected Datatype baseType;

    /**
     * Determines whether this datatype is a named datatype
     */
    protected boolean isNamed = false;

    /**
     * The dimensions of the ARRAY element of an ARRAY datatype.
     */
    protected long[] arrayDims;

    /**
     * Determines whether this datatype is a variable-length type.
     */
    protected boolean isVLEN = false;

    /**
     * Determines whether this datatype is a complex type.
     */
    protected boolean isComplex = false;

    /**
     * Determines whether this datatype is a variable-length string type.
     */
    protected boolean isVariableStr = false;

    /**
     * The (name, value) pairs of enum members.
     */
    protected Map<String, String> enumMembers;

    /**
     * The list of names of members of a compound Datatype.
     */
    protected List<String> compoundMemberNames;

    /**
     * The list of types of members of a compound Datatype.
     */
    protected List<Datatype> compoundMemberTypes;

    /**
     * The list of offsets of members of a compound Datatype.
     */
    protected List<Long> compoundMemberOffsets;

    /**
     * Constructs a named datatype with a given file, name and path.
     *
     * @param theFile
     *            the HDF file.
     * @param typeName
     *            the name of the datatype, e.g "12-bit Integer".
     * @param typePath
     *            the full group path of the datatype, e.g. "/datatypes/".
     */
    public Datatype(FileFormat theFile, String typeName, String typePath)
    {
        this(theFile, typeName, typePath, null);
    }

    /**
     * @deprecated Not for public use in the future.<br>
     *             Using {@link #Datatype(FileFormat, String, String)}
     *
     * @param theFile
     *            the HDF file.
     * @param typeName
     *            the name of the datatype, e.g "12-bit Integer".
     * @param typePath
     *            the full group path of the datatype, e.g. "/datatypes/".
     * @param oid
     *            the oidof the datatype.
     */
    @Deprecated
    public Datatype(FileFormat theFile, String typeName, String typePath, long[] oid)
    {
        super(theFile, typeName, typePath, oid);
    }

    /**
     * Constructs a Datatype with specified class, size, byte order and sign.
     *
     * The following is a list of a few examples of Datatype.
     * <ol>
     * <li>to create unsigned native integer<br>
     * Datatype type = new Dataype(Datatype.CLASS_INTEGER, Datatype.NATIVE, Datatype.NATIVE,
     * Datatype.SIGN_NONE); <li>to create 16-bit signed integer with big endian<br> Datatype type = new
     * Dataype(Datatype.CLASS_INTEGER, 2, Datatype.ORDER_BE, Datatype.NATIVE); <li>to create native float<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, Datatype.NATIVE, Datatype.NATIVE, Datatype.NATIVE);
     * <li>to create 64-bit double<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
     * </ol>
     *
     * @param tclass
     *            the class of the datatype, e.g. CLASS_INTEGER, CLASS_FLOAT and etc.
     * @param tsize
     *            the size of the datatype in bytes, e.g. for a 32-bit integer, the size is 4.
     *            Valid values are NATIVE or a positive value.
     * @param torder
     *            the byte order of the datatype. Valid values are ORDER_LE, ORDER_BE, ORDER_VAX,
     *            ORDER_NONE and NATIVE.
     * @param tsign
     *            the sign of the datatype. Valid values are SIGN_NONE, SIGN_2 and NATIVE.
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(int tclass, int tsize, int torder, int tsign) throws Exception
    {
        this(tclass, tsize, torder, tsign, null);
    }

    /**
     * Constructs a Datatype with specified class, size, byte order and sign.
     *
     * The following is a list of a few examples of Datatype.
     * <ol>
     * <li>to create unsigned native integer<br>
     * Datatype type = new Dataype(Datatype.CLASS_INTEGER, Datatype.NATIVE, Datatype.NATIVE,
     * Datatype.SIGN_NONE); <li>to create 16-bit signed integer with big endian<br> Datatype type = new
     * Dataype(Datatype.CLASS_INTEGER, 2, Datatype.ORDER_BE, Datatype.NATIVE); <li>to create native float<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, Datatype.NATIVE, Datatype.NATIVE, Datatype.NATIVE);
     * <li>to create 64-bit double<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
     * </ol>
     *
     * @param tclass
     *            the class of the datatype, e.g. CLASS_INTEGER, CLASS_FLOAT and
     *            etc.
     * @param tsize
     *            the size of the datatype in bytes, e.g. for a 32-bit integer,
     *            the size is 4.
     *            Valid values are NATIVE or a positive value.
     * @param torder
     *            the byte order of the datatype. Valid values are ORDER_LE,
     *            ORDER_BE, ORDER_VAX, ORDER_NONE and NATIVE.
     * @param tsign
     *            the sign of the datatype. Valid values are SIGN_NONE, SIGN_2 and NATIVE.
     * @param tbase
     *            the base datatype of the new datatype
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(int tclass, int tsize, int torder, int tsign, Datatype tbase) throws Exception
    {
        this(null, tclass, tsize, torder, tsign, tbase, null);
    }

    /**
     * Constructs a Datatype with specified class, size, byte order and sign.
     *
     * The following is a list of a few examples of Datatype.
     * <ol>
     * <li>to create unsigned native integer<br>
     * Datatype type = new Dataype(Datatype.CLASS_INTEGER, Datatype.NATIVE, Datatype.NATIVE,
     * Datatype.SIGN_NONE); <li>to create 16-bit signed integer with big endian<br> Datatype type = new
     * Dataype(Datatype.CLASS_INTEGER, 2, Datatype.ORDER_BE, Datatype.NATIVE); <li>to create native float<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, Datatype.NATIVE, Datatype.NATIVE, Datatype.NATIVE);
     * <li>to create 64-bit double<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
     * </ol>
     *
     * @param theFile
     *            the HDF file.
     * @param tclass
     *            the class of the datatype, e.g. CLASS_INTEGER, CLASS_FLOAT and etc.
     * @param tsize
     *            the size of the datatype in bytes, e.g. for a 32-bit integer, the size is 4.
     *            Valid values are NATIVE or a positive value.
     * @param torder
     *            the byte order of the datatype. Valid values are ORDER_LE, ORDER_BE, ORDER_VAX,
     *            ORDER_NONE and NATIVE.
     * @param tsign
     *            the sign of the datatype. Valid values are SIGN_NONE, SIGN_2 and NATIVE.
     * @param tbase
     *            the base datatype of the new datatype
     * @param pbase
     *            the parent datatype of the new datatype
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(FileFormat theFile, int tclass, int tsize, int torder, int tsign, Datatype tbase,
                    Datatype pbase) throws Exception
    {
        super(theFile, null, null, null);
        if ((tsize == 0) || (tsize < 0 && tsize != Datatype.NATIVE))
            throw new Exception("invalid datatype size - " + tsize);
        if ((torder != Datatype.ORDER_LE) && (torder != Datatype.ORDER_BE) &&
            (torder != Datatype.ORDER_VAX) && (torder != Datatype.ORDER_NONE) && (torder != Datatype.NATIVE))
            throw new Exception("invalid datatype order - " + torder);
        if ((tsign != Datatype.SIGN_NONE) && (tsign != Datatype.SIGN_2) && (tsign != Datatype.NATIVE))
            throw new Exception("invalid datatype sign - " + tsign);

        datatypeClass = tclass;
        datatypeSize  = tsize;
        if (datatypeSize == NATIVE)
            datatypeNATIVE = true;
        else
            datatypeNATIVE = false;
        datatypeOrder = torder;
        datatypeSign  = tsign;
        enumMembers   = null;
        baseType      = tbase;
        arrayDims     = null;
        isVariableStr = (datatypeClass == Datatype.CLASS_STRING) && (tsize < 0);
        isVLEN        = (datatypeClass == Datatype.CLASS_VLEN) || isVariableStr;

        compoundMemberNames   = new ArrayList<>();
        compoundMemberTypes   = new ArrayList<>();
        compoundMemberOffsets = new ArrayList<>();

        log.trace("datatypeClass={} datatypeSize={} datatypeOrder={} datatypeSign={} baseType={}",
                  datatypeClass, datatypeSize, datatypeOrder, datatypeSign, baseType);
    }

    /**
     * Constructs a Datatype with specified class, size, byte order and sign.
     *
     * The following is a list of a few examples of Datatype.
     * <ol>
     * <li>to create unsigned native integer<br>
     * Datatype type = new Dataype(Datatype.CLASS_INTEGER, Datatype.NATIVE, Datatype.NATIVE,
     * Datatype.SIGN_NONE); <li>to create 16-bit signed integer with big endian<br> Datatype type = new
     * Dataype(Datatype.CLASS_INTEGER, 2, Datatype.ORDER_BE, Datatype.NATIVE); <li>to create native float<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, Datatype.NATIVE, Datatype.NATIVE, Datatype.NATIVE);
     * <li>to create 64-bit double<br>
     * Datatype type = new Dataype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, Datatype.NATIVE);
     * </ol>
     *
     * @param tclass
     *            the class of the datatype, e.g. CLASS_INTEGER, CLASS_FLOAT and etc.
     * @param tsize
     *            the size of the datatype in bytes, e.g. for a 32-bit integer, the size is 4.
     *            Valid values are NATIVE or a positive value.
     * @param torder
     *            the byte order of the datatype. Valid values are ORDER_LE, ORDER_BE, ORDER_VAX,
     *            ORDER_NONE and NATIVE.
     * @param tsign
     *            the sign of the datatype. Valid values are SIGN_NONE, SIGN_2 and NATIVE.
     * @param tbase
     *            the base datatype of the new datatype
     * @param pbase
     *            the parent datatype of the new datatype
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(int tclass, int tsize, int torder, int tsign, Datatype tbase, Datatype pbase)
        throws Exception
    {
        this(null, tclass, tsize, torder, tsign, tbase, pbase);
    }

    /**
     * Constructs a Datatype with a given native datatype identifier.
     *
     * For example, if the datatype identifier is a 32-bit unsigned integer created from HDF5,
     *
     * <pre>
     * long tid = H5.H5Tcopy(HDF5Constants.H5T_NATIVE_UNINT32);
     * Datatype dtype = new Datatype(tid);
     * </pre>
     *
     * will construct a datatype equivalent to new Datatype(CLASS_INTEGER, 4, NATIVE, SIGN_NONE);
     *
     * @see #fromNative(long tid)
     * @param theFile
     *            the HDF file.
     * @param tid
     *            the native datatype identifier.
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(FileFormat theFile, long tid) throws Exception { this(theFile, tid, null); }

    /**
     * Constructs a Datatype with a given native datatype identifier.
     *
     * For example, if the datatype identifier is a 32-bit unsigned integer created from HDF5,
     *
     * <pre>
     * long tid = H5.H5Tcopy(HDF5Constants.H5T_NATIVE_UNINT32);
     * Datatype dtype = new Datatype(tid);
     * </pre>
     *
     * will construct a datatype equivalent to new Datatype(CLASS_INTEGER, 4, NATIVE, SIGN_NONE);
     *
     * @see #fromNative(long tid)
     * @param theFile
     *            the HDF file.
     * @param tid
     *            the native datatype identifier.
     * @param pbase
     *            the parent datatype of the new datatype
     *
     * @throws Exception
     *            if there is an error
     */
    public Datatype(FileFormat theFile, long tid, Datatype pbase) throws Exception
    {
        this(theFile, CLASS_NO_CLASS, NATIVE, NATIVE, NATIVE, null, pbase);
    }

    /**
     * Opens access to this named datatype. Sub-classes must replace this default implementation. For
     * example, in H5Datatype, open() function H5.H5Topen(loc_id, name) to get the datatype identifier.
     *
     * @return the datatype identifier if successful; otherwise returns negative value.
     */
    @Override
    public long open()
    {
        return -1;
    }

    /**
     * Closes a datatype identifier.
     *
     * Sub-classes must replace this default implementation.
     *
     * @param id
     *            the datatype identifier to close.
     */
    @Override
    public abstract void close(long id);

    /**
     * Returns the class of the datatype. Valid values are:
     * <ul>
     * <li>CLASS_NO_CLASS
     * <li>CLASS_INTEGER
     * <li>CLASS_FLOAT
     * <li>CLASS_CHAR
     * <li>CLASS_STRING
     * <li>CLASS_BITFIELD
     * <li>CLASS_OPAQUE
     * <li>CLASS_COMPOUND
     * <li>CLASS_REFERENCE
     * <li>CLASS_ENUM
     * <li>CLASS_VLEN
     * <li>CLASS_ARRAY
     * <li>CLASS_COMPLEX
     * </ul>
     *
     * @return the class of the datatype.
     */
    public int getDatatypeClass() { return datatypeClass; }

    /**
     * Returns the size of the datatype in bytes. For example, for a 32-bit
     * integer, the size is 4 (bytes).
     *
     * @return the size of the datatype.
     */
    public long getDatatypeSize() { return datatypeSize; }

    /**
     * Returns the byte order of the datatype. Valid values are
     * <ul>
     * <li>ORDER_LE
     * <li>ORDER_BE
     * <li>ORDER_VAX
     * <li>ORDER_NONE
     * </ul>
     *
     * @return the byte order of the datatype.
     */
    public int getDatatypeOrder() { return datatypeOrder; }

    /**
     * Returns the sign (SIGN_NONE, SIGN_2) of an integer datatype.
     *
     * @return the sign of the datatype.
     */
    public int getDatatypeSign() { return datatypeSign; }

    /**
     * Returns the base datatype for this datatype.
     *
     * For example, in a dataset of type ARRAY of integer, the datatype of the dataset is ARRAY. The
     * datatype of the base type is integer.
     *
     * @return the datatype of the contained basetype.
     */
    public Datatype getDatatypeBase() { return baseType; }

    /**
     * Sets the (key, value) pairs of enum members for enum datatype.
     *
     * For Example,
     * <dl>
     * <dt>setEnumMembers("-40=lowTemp, 90=highTemp")</dt>
     * <dd>sets the key of enum member lowTemp to -40 and highTemp to 90.</dd>
     * <dt>setEnumMembers("lowTemp, highTemp")</dt>
     * <dd>sets enum members to defaults, i.e. 0=lowTemp and 1=highTemp</dd>
     * <dt>setEnumMembers("10=lowTemp, highTemp")</dt>
     * <dd>sets enum member lowTemp to 10 and highTemp to 11.</dd>
     * </dl>
     *
     * @param enumStr
     *            the (key, value) pairs of enum members
     */
    public final void setEnumMembers(String enumStr)
    {
        log.trace("setEnumMembers: start enum_members={}", enumStr);
        if (enumStr != null) {
            enumMembers      = new HashMap<>();
            String[] entries = enumStr.split(",");
            for (String entry : entries) {
                String[] keyValue = entry.split("=");
                enumMembers.put(keyValue[0].trim(), keyValue[1].trim());
                if (log.isTraceEnabled())
                    log.trace("setEnumMembers: value={} name={}", keyValue[0].trim(), keyValue[1].trim());
            }
        }
        datatypeDescription = null; // reset description
        log.trace("setEnumMembers: finish enum size={}", enumMembers.size());
    }

    /**
     * Returns the Map&lt;String,String&gt; pairs of enum members for enum datatype.
     *
     * @return enumStr Map&lt;String,String%gt; pairs of enum members
     */
    public final Map<String, String> getEnumMembers()
    {
        if (enumMembers == null) {
            log.trace("getEnumMembers: null");
            enumMembers = new HashMap<>();
        }

        return enumMembers;
    }

    /**
     * Returns the HashMap pairs of enum members for enum datatype.
     *
     * For Example,
     * <dl>
     * <dt>getEnumMembersAsString()</dt>
     * <dd>returns "10=lowTemp, 40=highTemp"</dd>
     * </dl>
     *
     * @return enumStr the (key, value) pairs of enum members
     */
    @SuppressWarnings("rawtypes")
    public final String getEnumMembersAsString()
    {
        StringBuilder enumStr = new StringBuilder();
        if (getEnumMembers() != null) {
            Iterator<Entry<String, String>> entries = enumMembers.entrySet().iterator();
            int i                                   = enumMembers.size();
            log.trace("getEnumMembersAsString: enum size={}", i);
            while (entries.hasNext()) {
                Entry thisEntry = entries.next();
                enumStr.append((String)thisEntry.getKey()).append("=").append((String)thisEntry.getValue());

                i--;
                if (i > 0)
                    enumStr.append(", ");
            }
        }
        log.trace("getEnumMembersAsString: finish {}", enumStr);
        return enumStr.toString();
    }

    /**
     * Returns the dimensions of an Array Datatype.
     *
     * @return dims the dimensions of the Array Datatype
     */
    public final long[] getArrayDims() { return arrayDims; }

    /**
     * Returns the member names of a Compound Datatype.
     *
     * @return member names of a Compound Datatype
     */
    public final List<String> getCompoundMemberNames() { return compoundMemberNames; }

    /**
     * Returns member types of a Compound Datatype.
     *
     * @return member types of a Compound Datatype
     */
    public final List<Datatype> getCompoundMemberTypes() { return compoundMemberTypes; }

    /**
     * Returns the member offsets of a Compound Datatype.
     *
     * @return member offsets of a Compound Datatype
     */
    public final List<Long> getCompoundMemberOffsets() { return compoundMemberOffsets; }

    /**
     * Converts the datatype object to a native datatype.
     *
     * Subclasses must implement it so that this datatype will be converted accordingly. Use close() to
     * close the native identifier; otherwise, the datatype will be left open.
     *
     * For example, a HDF5 datatype created from<br>
     *
     * <pre>
     * H5Dataype dtype = new H5Datatype(CLASS_INTEGER, 4, NATIVE, SIGN_NONE);
     * int tid = dtype.createNative();
     * </pre>
     *
     * The "tid" will be the HDF5 datatype id of a 64-bit unsigned integer, which is equivalent to
     *
     * <pre>
     * int tid = H5.H5Tcopy(HDF5Constants.H5T_NATIVE_UNINT32);
     * </pre>
     *
     * @return the identifier of the native datatype.
     */
    public abstract long createNative();

    /**
     * Set datatype characteristics (class, size, byte order and sign) from a given datatype identifier.
     *
     * Sub-classes must implement it so that this datatype will be converted accordingly.
     *
     * For example, if the type identifier is a 64-bit unsigned integer created from HDF5,
     *
     * <pre>
     * H5Datatype dtype = new H5Datatype();
     * dtype.fromNative(HDF5Constants.H5T_NATIVE_UNINT32);
     * </pre>
     *
     * Where dtype is equivalent to <br>
     * new H5Datatype(CLASS_INTEGER, 4, NATIVE, SIGN_NONE);
     *
     * @param nativeID
     *            the datatype identifier.
     */
    public abstract void fromNative(long nativeID);

    /**
     * If the datatype is a reference, then return the type.
     *
     * @return the datatype reference type if successful; otherwise returns negative value.
     */
    public long getReferenceType() { return -1; }

    /**
     * Returns a short text description of this datatype.
     *
     * @return a short text description of this datatype
     */
    public String getDescription()
    {
        if (datatypeDescription != null)
            return datatypeDescription;

        StringBuilder description = new StringBuilder();

        switch (datatypeClass) {
        case CLASS_CHAR:
            description.append("8-bit ").append((isUnsigned() ? "unsigned " : "")).append("integer");
            break;
        case CLASS_INTEGER:
            log.trace("getDescription(): Int [{}]", datatypeNATIVE);
            if (datatypeNATIVE)
                description.append("native ").append((isUnsigned() ? "unsigned " : "")).append("integer");
            else
                description.append(String.valueOf(datatypeSize * 8))
                    .append("-bit ")
                    .append((isUnsigned() ? "unsigned " : ""))
                    .append("integer");
            break;
        case CLASS_FLOAT:
            if (datatypeNATIVE)
                description.append("native floating-point");
            else
                description.append(String.valueOf(datatypeSize * 8)).append("-bit floating-point");
            break;
        case CLASS_STRING:
            description.append("String");
            break;
        case CLASS_REFERENCE:
            description.append("Object reference");
            break;
        case CLASS_OPAQUE:
            if (datatypeNATIVE)
                description.append("native opaque");
            else
                description.append(String.valueOf(datatypeSize * 8)).append("-bit opaque");
            break;
        case CLASS_BITFIELD:
            if (datatypeNATIVE)
                description.append("native bitfield");
            else
                description.append(String.valueOf(datatypeSize * 8)).append("-bit bitfield");
            break;
        case CLASS_ENUM:
            if (datatypeNATIVE)
                description.append("native enum");
            else
                description.append(String.valueOf(datatypeSize * 8)).append("-bit enum");
            break;
        case CLASS_ARRAY:
            description.append("Array");

            if (arrayDims != null) {
                description.append(" [");
                for (int i = 0; i < arrayDims.length; i++) {
                    description.append(arrayDims[i]);
                    if (i < arrayDims.length - 1)
                        description.append(" x ");
                }
                description.append("]");
            }

            break;
        case CLASS_COMPOUND:
            description.append("Compound");
            break;
        case CLASS_VLEN:
            description.append("Variable-length");
            break;
        case CLASS_COMPLEX:
            log.trace("getDescription(): Complex [{}]", datatypeNATIVE);
            if (datatypeNATIVE)
                description.append("native Complex");
            else
                description.append(String.valueOf(datatypeSize * 8)).append("-bit Complex");
            break;
        default:
            description.append("Unknown");
            break;
        }

        if (baseType != null)
            description.append(" of " + baseType.getDescription());

        return description.toString();
    }

    /**
     * Checks if this datatype is unsigned.
     *
     * @return true if the datatype is unsigned;
     *         otherwise, returns false.
     */
    public boolean isUnsigned()
    {
        if (baseType != null)
            return baseType.isUnsigned();
        else {
            if (isCompound()) {
                if ((compoundMemberTypes != null) && !compoundMemberTypes.isEmpty()) {
                    boolean allMembersUnsigned = true;

                    Iterator<Datatype> cmpdTypeListIT = compoundMemberTypes.iterator();
                    while (cmpdTypeListIT.hasNext()) {
                        Datatype next = cmpdTypeListIT.next();

                        allMembersUnsigned = allMembersUnsigned && next.isUnsigned();
                    }

                    return allMembersUnsigned;
                }
                else {
                    log.debug("isUnsigned(): compoundMemberTypes is null");
                    return false;
                }
            }
            else {
                return (datatypeSign == Datatype.SIGN_NONE);
            }
        }
    }

    /**
     * Checks if this datatype is a boolean type.
     *
     * @return true if the datatype is boolean; false otherwise
     */
    public abstract boolean isText();

    /**
     * Checks if this datatype is an integer type.
     *
     * @return true if the datatype is integer; false otherwise
     */
    public boolean isInteger() { return (datatypeClass == Datatype.CLASS_INTEGER); }

    /**
     * Checks if this datatype is a floating-point type.
     *
     * @return true if the datatype is floating-point; false otherwise
     */
    public boolean isFloat() { return (datatypeClass == Datatype.CLASS_FLOAT); }

    /**
     * Checks if this datatype is a named type.
     *
     * @return true if the datatype is named; false otherwise
     */
    public boolean isNamed() { return isNamed; }

    /**
     * Checks if this datatype is a variable-length string type.
     *
     * @return true if the datatype is variable-length string; false otherwise
     */
    public boolean isVarStr() { return isVariableStr; }

    /**
     * Checks if this datatype is a variable-length type.
     *
     * @return true if the datatype is variable-length; false otherwise
     */
    public boolean isVLEN() { return isVLEN; }

    /**
     * Checks if this datatype is an compound type.
     *
     * @return true if the datatype is compound; false otherwise
     */
    public boolean isCompound() { return (datatypeClass == Datatype.CLASS_COMPOUND); }

    /**
     * Checks if this datatype is an array type.
     *
     * @return true if the datatype is array; false otherwise
     */
    public boolean isArray() { return (datatypeClass == Datatype.CLASS_ARRAY); }

    /**
     * Checks if this datatype is a string type.
     *
     * @return true if the datatype is string; false otherwise
     */
    public boolean isString() { return (datatypeClass == Datatype.CLASS_STRING); }

    /**
     * Checks if this datatype is a character type.
     *
     * @return true if the datatype is character; false otherwise
     */
    public boolean isChar() { return (datatypeClass == Datatype.CLASS_CHAR); }

    /**
     * Checks if this datatype is a reference type.
     *
     * @return true if the datatype is reference; false otherwise
     */
    public boolean isRef() { return (datatypeClass == Datatype.CLASS_REFERENCE); }

    /**
     * Checks if this datatype is a enum type.
     *
     * @return true if the datatype is enum; false otherwise
     */
    public boolean isEnum() { return (datatypeClass == Datatype.CLASS_ENUM); }

    /**
     * Checks if this datatype is a opaque type.
     *
     * @return true if the datatype is opaque; false otherwise
     */
    public boolean isOpaque() { return (datatypeClass == Datatype.CLASS_OPAQUE); }

    /**
     * Checks if this datatype is a bitfield type.
     *
     * @return true if the datatype is bitfield; false otherwise
     */
    public boolean isBitField() { return (datatypeClass == Datatype.CLASS_BITFIELD); }

    /**
     * Checks if this datatype is a complex type.
     *
     * @return true if the datatype is complex; false otherwise
     */
    public boolean isComplex() { return (datatypeClass == Datatype.CLASS_COMPLEX); }

    /* Implement interface MetaDataContainer */

    /**
     * Removes all of the elements from metadata list.
     * The list should be empty after this call returns.
     */
    @Override
    public void clear()
    {
    }

    /**
     * Retrieves the object's metadata, such as attributes, from the file.
     *
     * Metadata, such as attributes, is stored in a List.
     *
     * @return the list of metadata objects.
     *
     * @throws Exception
     *             if the metadata can not be retrieved
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List getMetadata() throws Exception
    {
        return null;
    }

    /**
     * Writes a specific piece of metadata (such as an attribute) into the file.
     *
     * If an HDF(4&amp;5) attribute exists in the file, this method updates its
     * value. If the attribute does not exist in the file, it creates the
     * attribute in the file and attaches it to the object. It will fail to
     * write a new attribute to the object where an attribute with the same name
     * already exists. To update the value of an existing attribute in the file,
     * one needs to get the instance of the attribute by getMetadata(), change
     * its values, then use writeMetadata() to write the value.
     *
     * @param info
     *            the metadata to write.
     *
     * @throws Exception
     *             if the metadata can not be written
     */
    @Override
    public void writeMetadata(Object info) throws Exception
    {
        throw new UnsupportedOperationException(
            "Unsupported operation. Subclasses must implement Datatype:writeMetadata.");
    }

    /**
     * Deletes an existing piece of metadata from this object.
     *
     * @param info
     *            the metadata to delete.
     *
     * @throws Exception
     *             if the metadata can not be removed
     */
    @Override
    public void removeMetadata(Object info) throws Exception
    {
        throw new UnsupportedOperationException(
            "Unsupported operation. Subclasses must implement Datatype:removeMetadata.");
    }

    /**
     * Updates an existing piece of metadata attached to this object.
     *
     * @param info
     *            the metadata to update.
     *
     * @throws Exception
     *             if the metadata can not be updated
     */
    @Override
    public void updateMetadata(Object info) throws Exception
    {
        throw new UnsupportedOperationException(
            "Unsupported operation. Subclasses must implement Datatype:updateMetadata.");
    }

    @Override
    public String toString()
    {
        return getDescription();
    }
}
