package com.smeo.tools.gc.newparser;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionEventParser implements GcElementParser {
    public boolean isMajorCollection;
    public boolean isSystemTriggered;

    CollectorEventParser youngGenCollector;
    CollectorEventParser oldGenCollector;
    CollectorEventParser permGenCollector;

    @Override
    public boolean needsMoreInfo(String loggedLine) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
