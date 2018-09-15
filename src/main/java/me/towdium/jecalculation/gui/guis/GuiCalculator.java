package me.towdium.jecalculation.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.algorithm.CostList;
import me.towdium.jecalculation.algorithm.CostList.Calculator;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer {
    enumMode mode = enumMode.INPUT;
    Calculator calculator = null;
    WLabelGroup lgRecent = new WLabelGroup(7, 31, 8, 1, WLabel.enumMode.PICKER);
    WLabel lLabel = new WLabel(31, 7, 20, 20, WLabel.enumMode.SELECTOR);
    WLabelScroll lsResult = new WLabelScroll(7, 87, 8, 4, WLabel.enumMode.RESULT, true);
    WButton btnSteps = new WButtonIcon(64, 62, 20, 20, Resource.BTN_LIST_N, Resource.BTN_LIST_F,
            Resource.BTN_LIST_D, "calculator.step").setListenerLeft(() -> setMode(enumMode.STEPS));
    WButton btnCatalyst = new WButtonIcon(45, 62, 20, 20, Resource.BTN_CAT_N, Resource.BTN_CAT_F,
            Resource.BTN_CAT_D, "calculator.catalyst").setListenerLeft(() -> setMode(enumMode.CATALYST));
    WButton btnOutput = new WButtonIcon(26, 62, 20, 20, Resource.BTN_OUT_N, Resource.BTN_OUT_F,
            Resource.BTN_OUT_D, "calculator.output").setListenerLeft(() -> setMode(enumMode.OUTPUT));
    WButton btnInput = new WButtonIcon(7, 62, 20, 20, Resource.BTN_IN_N, Resource.BTN_IN_F,
            Resource.BTN_IN_D, "calculator.input").setListenerLeft(() -> setMode(enumMode.INPUT));
    WTextField tfAmount = new WTextField(60, 7, 65);

    public GuiCalculator() {
        lLabel.setLsnrUpdate(() -> {
            Controller.setRecent(lLabel.label);
            refreshRecent();
            refreshCalculator();
        });
        lgRecent.setLsnrUpdate(l -> JecaGui.getCurrent().hand = lgRecent.getLabelAt(l));
        tfAmount.setLsnrText(s -> refreshCalculator());
        add(new WPanel());
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "calculator.label")
                .setListenerLeft(() -> JecaGui.displayGui(new GuiLabel(l -> {
                    JecaGui.displayParent();
                    JecaGui.getCurrent().hand = l;
                }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "calculator.recipe")
                .setListenerLeft(() -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "calculator.search")
                .setListenerLeft(() -> JecaGui.displayGui(new GuiSearch())));
        add(new WText(53, 13, JecaGui.Font.DEFAULT_NO_SHADOW, "x"));
        add(new WLine(55));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "calculator.history"));
        addAll(lgRecent, lLabel, btnInput, btnOutput, btnCatalyst, btnSteps, lsResult, tfAmount);
        refreshRecent();
        setMode(enumMode.INPUT);
        refreshCalculator();
    }

    void setMode(enumMode mode) {
        this.mode = mode;
        btnInput.setDisabled(mode == enumMode.INPUT);
        btnOutput.setDisabled(mode == enumMode.OUTPUT);
        btnCatalyst.setDisabled(mode == enumMode.CATALYST);
        btnSteps.setDisabled(mode == enumMode.STEPS);
        refreshResult();
    }

    void refreshRecent() {
        List<ILabel> recent = Controller.getRecent();
        if (!recent.isEmpty()) lLabel.setLabel(recent.get(0));
        if (recent.size() > 1) lgRecent.setLabel(recent.subList(1, recent.size()), 0);
    }

    void refreshCalculator() {
        try {
            String s = tfAmount.getText();
            int i = s.isEmpty() ? 1 : Integer.parseInt(tfAmount.getText());
            tfAmount.setColor(JecaGui.COLOR_TEXT_WHITE);
            calculator = new CostList(lLabel.getLabel().copy().setAmount(i)).calculate();
        } catch (NumberFormatException e) {
            tfAmount.setColor(JecaGui.COLOR_TEXT_RED);
            calculator = null;
        }
        refreshResult();
    }

    void refreshResult() {
        if (calculator == null) {
            lsResult.setLabels(new ArrayList<>());
        } else {
            switch (mode) {
                case INPUT:
                    lsResult.setLabels(calculator.getInputs());
                    break;
                case OUTPUT:
                    lsResult.setLabels(new ArrayList<>());
                    break;
                case CATALYST:
                    lsResult.setLabels(calculator.getCatalysts());
                    break;
                case STEPS:
                    lsResult.setLabels(new ArrayList<>());
                    break;
            }
        }
    }

    enum enumMode {
        INPUT, OUTPUT, CATALYST, STEPS
    }
}
