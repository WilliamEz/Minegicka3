package com.williameze.api.gui;

import java.util.ArrayList;
import java.util.List;

public interface IGuiHasScrollPanel
{
    public List<PanelScrollList> panels = new ArrayList();

    public void panelObjClickedFeedback(ScrollObject obj);

    public void panelObjHoverFeedback(ScrollObject obj);
    
    public boolean drawPanelBackground(PanelScrollList panel);
}
