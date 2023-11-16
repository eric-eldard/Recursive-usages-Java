package treeOfUsages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.ide.ActivityTracker;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.action.CollapseTreeAction;
import treeOfUsages.action.EnableableAction;
import treeOfUsages.action.ExpandTreeAction;
import treeOfUsages.action.FindUsagesAction;
import treeOfUsages.action.FindDirectUsageAction;
import treeOfUsages.action.FindUsagesIncludingChildrenAction;
import treeOfUsages.action.FindUsagesIncludingParentsAction;
import treeOfUsages.action.FindUsagesIncludingParentsAndChildrenAction;
import treeOfUsages.action.StopFindUsagesAction;
import treeOfUsages.util.TreeGenerator;

/**
 * Note that this warning may be logged when debugging and that it has no impact on IntelliJ or the plugin:
 * <a href="https://github.com/JetBrains/gradle-intellij-plugin/issues/777">
 *     Please call toolbar.setTargetComponent() explicitly
 * </a>
 */
public class Plugin
{
    private final JPanel generalPanel;

    private final JPanel bottomPanel;

    private final JBLoadingPanel loadingPanel;

    private final Project project;

    private Tree tree;

    private JBScrollPane treeView;

    private BackgroundableProcessIndicator progressIndicator;

    public boolean forcedCancel = false;

    // Actions
    private final FindUsagesAction findDirectUsagesAction = new FindDirectUsageAction(this);

    private final FindUsagesAction findUsagesIncludingParentsAction = new FindUsagesIncludingParentsAction(this);

    private final FindUsagesAction findUsagesIncludingChildrenAction = new FindUsagesIncludingChildrenAction(this);

    private final FindUsagesAction findUsagesIncludingParentsAndChildrenAction = 
        new FindUsagesIncludingParentsAndChildrenAction(this);

    private final StopFindUsagesAction stopFindUsagesAction = new StopFindUsagesAction(this);

    private final ExpandTreeAction expandTreeAction = new ExpandTreeAction(this);

    private final CollapseTreeAction collapseTreeAction = new CollapseTreeAction(this);

    private final List<EnableableAction> allActions = new ArrayList<>();


    public Plugin(Project p)
    {
        project = p;
        generalPanel = new JPanel(new BorderLayout());

        allActions.add(findDirectUsagesAction);
        allActions.add(findUsagesIncludingParentsAction);
        allActions.add(findUsagesIncludingChildrenAction);
        allActions.add(findUsagesIncludingParentsAndChildrenAction);
        allActions.add(stopFindUsagesAction);
        allActions.add(expandTreeAction);
        allActions.add(collapseTreeAction);

        JComponent toolbarPanel = createToolbarPanel();
        generalPanel.add(toolbarPanel, BorderLayout.NORTH);

        bottomPanel = new JPanel(new BorderLayout());
        generalPanel.add(bottomPanel, BorderLayout.CENTER);

        treeView = new JBScrollPane();

        Disposable animationDisposable = Disposer.newDisposable();
        loadingPanel = new JBLoadingPanel(new FlowLayout(), animationDisposable);
        loadingPanel.startLoading();
    }

    private void setLoading(boolean isLoading)
    {
        bottomPanel.removeAll();

        bottomPanel.add(isLoading ? loadingPanel : treeView);

        allActions.stream()
            .filter(action -> action != stopFindUsagesAction)
            .forEach(action -> action.setEnabled(!isLoading));

        stopFindUsagesAction.setEnabled(isLoading);

        ActivityTracker.getInstance().inc();
    }

    @NotNull
    private JComponent createToolbarPanel()
    {
        DefaultActionGroup result = new DefaultActionGroup();

        result.addAll(allActions);

        return ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.STRUCTURE_VIEW_TOOLBAR, result, true)
            .getComponent();
    }

    public void createAndRenderTree(PsiMethodImpl element, boolean includeSupers, boolean includeOverrides)
    {
        setLoading(true);

        TreeGenerator treeGenerator = new TreeGenerator(this, project, element, includeSupers, includeOverrides);

        progressIndicator = new BackgroundableProcessIndicator(treeGenerator);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(treeGenerator, progressIndicator);
    }

    public void finishCreatingTree(Tree tree)
    {
        this.tree = tree;
        treeView = new JBScrollPane(tree);
        setLoading(false);
    }

    public void stop()
    {
        tree = null;
        treeView.removeAll();
        forcedCancel = true;
        progressIndicator.cancel();
        setLoading(false);
    }

    public JPanel getContent()
    {
        return generalPanel;
    }

    public Tree getTree()
    {
        return tree;
    }
}