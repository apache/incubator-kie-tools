package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import com.ait.tooling.common.api.json.JSONType;
import com.ait.tooling.nativetools.client.NJSONReplacer;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory array implementation stub for class <code>com.ait.tooling.nativetools.client.NArrayBaseJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass( "com.ait.tooling.nativetools.client.NArrayBaseJSO" )
public class NArrayBaseJSO<T extends NArrayBaseJSO<T>> extends JavaScriptObject
{

    protected final List<Object> list = new ArrayList<>();
    
    @SuppressWarnings("unchecked")
    protected static <T extends NArrayBaseJSO<T>> T createNArrayBaseJSO()
    {
        return (T) new NArrayBaseJSO<>();
    }

    protected NArrayBaseJSO()
    {
    }

    public JSONArray toJSONArray()
    {
        // TODO
        return null;
    }

    public String toJSONString()
    {
        // TODO
        return "";
    }

    public String toJSONString(final NJSONReplacer replacer)
    {
        // TODO
        return "";
    }

    public String toJSONString(final String indent)
    {
        // TODO
        return "";
    }

    public String toJSONString(final NJSONReplacer replacer, final String indent)
    {
        // TODO
        return "";
    }

    public String toJSONString(final int indent)
    {
        // TODO
        return "";
    }

    public String toJSONString(final NJSONReplacer replacer, final int indent)
    {
        // TODO
        return "";
    }

    public void clear()
    {
        setSize(0);
    }

    public String join()
    {
        return join(",");
    }

    public JSONType getNativeTypeOf(final int index)
    {
        // TODO
        return null;
    }

    public boolean isNull(final int index)
    {
        if ((index < 0) || (index >= size()))
        {
            return true;
        }
        return isNull_0(index);
    }

    public boolean isDefined(final int index)
    {
        if ((index < 0) || (index >= size()))
        {
            return false;
        }
        return isDefined_0(index);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void setSize(int size) {
        // Does not makes sense here.
    }

    public void splice(int beg, int removed) {
        // TODO
    }

    public void reverse() {
        // TODO
    }

    public String join(String separator) {
        return StringUtils.join( list, separator);
    }

    public T concat(T value) {
        // TODO
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public T copy() {
        NArrayBaseJSO result = new NArrayBaseJSO<>();
        result.list.addAll( list );
        
        return (T) result;
    }

    public T slice(int beg) {
        // TODO
        return null;
    }

    public T slice(int beg, int end) {
        // TODO
        return null;
    }

    private boolean isNull_0(int index) {
        return list.size() <= index || null == list.get( index );
    }

    private boolean isDefined_0(int index) {
        return list.size() > index && null != list.get( index );
    }

}
