package com.change_vision.astah;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IViewManager;

public class ZoomAndPanView extends JPanel implements IPluginExtraTabView, ProjectEventListener {

    private static final long serialVersionUID = 1L;

    JSlider slider;

    public ZoomAndPanView() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(createLabelPane(), BorderLayout.CENTER);
        addProjectEventListener();
    }

    private void addProjectEventListener() {
        try {
            AstahAPI api = AstahAPI.getAstahAPI();
            ProjectAccessor projectAccessor = api.getProjectAccessor();
            projectAccessor.addProjectEventListener(this);
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
    }

    private Container createLabelPane() {
        int minPercentage = 5;
        int maxPercentage = 400;
        slider = new JSlider(minPercentage, maxPercentage);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double zoomValue = slider.getValue() / 100.0;
                IViewManager viewManager = null;
                try {
                    viewManager = AstahAPI.getAstahAPI().getViewManager();
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (InvalidUsingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (viewManager == null) {
                    return;
                }
                IDiagramViewManager diagramViewManager = viewManager.getDiagramViewManager();
                if (diagramViewManager == null) {
                    return;
                }
                diagramViewManager.zoom(zoomValue, true);
                IPresentation[] selectedPresentations = diagramViewManager
                        .getSelectedPresentations();
                for (IPresentation presentation : selectedPresentations) {

                    // diagramViewManager.showInDiagramEditor(presentation);
                    // return;

                    if (presentation instanceof INodePresentation) {
                        INodePresentation nodePs = INodePresentation.class.cast(presentation);
                        Point2D location = nodePs.getLocation();
                        int vectorX = diagramViewManager.toDeviceCoordX(location.getX());
                        int vectorY = diagramViewManager.toDeviceCoordY(location.getY());
                        diagramViewManager.pan(Double.valueOf(vectorX), Double.valueOf(vectorY));
                        return;
                    }
                    
                }
            }
        });

        JPanel p = new JPanel();
        p.add(slider);
        return new JScrollPane(p);
    }

    @Override
    public void projectChanged(ProjectEvent e) {
    }

    @Override
    public void projectClosed(ProjectEvent e) {
    }

    @Override
    public void projectOpened(ProjectEvent e) {
    }

    @Override
    public void addSelectionListener(ISelectionListener listener) {
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getDescription() {
        return "Zoom is carried out, displaying the selected element on the upper left. ";
    }

    @Override
    public String getTitle() {
        return "Zoom and Pan View";
    }

    public void activated() {
    }

    public void deactivated() {
    }

}