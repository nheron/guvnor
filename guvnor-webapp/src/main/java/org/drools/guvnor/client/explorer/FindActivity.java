package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.drools.guvnor.client.rulelist.QueryWidget;

public class FindActivity extends AbstractActivity {

    public void start( AcceptsOneWidget panel, EventBus eventBus ) {

        panel.setWidget( new QueryWidget() );
    }
}