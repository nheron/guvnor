/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.simulation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.shared.simulation.SimulationModel;
import org.drools.guvnor.shared.simulation.SimulationPathModel;
import org.drools.guvnor.shared.simulation.SimulationStepModel;

import java.util.HashMap;
import java.util.Map;

public class SimulationTestEditor extends Composite
        implements EditorWidget, SimulationTestEventHandler {

    protected interface SimulationTestEditorBinder extends UiBinder<Widget, SimulationTestEditor> {}
    private static SimulationTestEditorBinder uiBinder = GWT.create(SimulationTestEditorBinder.class);

    @UiField
    protected PushButton runSimulationButton;

//    @UiField
//    protected PushButton debugSimulationButton;

    @UiField
    protected TabPanel pathTabPanel;
    private Map<SimulationPathModel, PathWidget> pathWidgetMap = new HashMap<SimulationPathModel, PathWidget>();

    @UiField(provided = true)
    protected TimeLineWidget timeLineWidget;

    private final Asset asset;

    public SimulationTestEditor(Asset asset, RuleViewer ruleViewer, ClientFactory clientFactory, EventBus eventBus) {
        this(asset);
    }

    public SimulationTestEditor(Asset asset) {
        this.asset = asset;
        timeLineWidget = new TimeLineWidget(this);
        initWidget(uiBinder.createAndBindUi(this));
        SimulationModel simulation = (SimulationModel) asset.getContent();
        for (SimulationPathModel path : simulation.getPaths().values()) {
            PathWidget pathWidget = new PathWidget(path, this);
            pathTabPanel.add(pathWidget, path.getName());
            pathWidgetMap.put(path, pathWidget);
        }
        pathTabPanel.selectTab(0);
        timeLineWidget.setSimulation(simulation);
    }

    public void addStep(SimulationPathModel path) {
        SimulationStepModel step = SimulationStepModel.createNew(path);
        path.addStep(step);
        pathWidgetMap.get(path).addedStep(step);
        timeLineWidget.addedStep(step);
    }

    public void removeStep(SimulationStepModel step) {
        SimulationPathModel path = step.getPath();
        path.removeStep(step);
        pathWidgetMap.get(path).removedStep(step);
        timeLineWidget.removedStep(step);
    }

}
