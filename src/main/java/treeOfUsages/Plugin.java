package treeOfUsages;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import one.util.streamex.StreamEx;
import treeOfUsages.action.CollapseTreeAction;
import treeOfUsages.action.EnableableAction;
import treeOfUsages.action.ExpandTreeAction;
import treeOfUsages.action.FindDirectUsageAction;
import treeOfUsages.action.FindUsagesIncludingChildrenAction;
import treeOfUsages.action.FindUsagesIncludingParentsAction;
import treeOfUsages.action.FindUsagesIncludingParentsAndChildrenAction;
import treeOfUsages.action.GoBackAction;
import treeOfUsages.action.GoForwardAction;
import treeOfUsages.action.ResetAction;
import treeOfUsages.action.ShowKeyAction;
import treeOfUsages.action.StopFindUsagesAction;
import treeOfUsages.generator.KeyTreeGenerator;
import treeOfUsages.generator.TreeGenerator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Plugin
{
    private final JPanel generalPanel;

    private final JPanel bottomPanel;

    private final JBLoadingPanel loadingPanel;

    private final Project project;

    private Tree tree;

    private JBScrollPane treeView;

    private ProgressIndicator progressIndicator;

    private boolean userCanceled = false;

    private final EnableableAction stopFindUsagesAction = new StopFindUsagesAction(this);

    private final EnableableAction goBackAction = new GoBackAction(this);

    private final EnableableAction goForwardAction = new GoForwardAction(this);

    private final EnableableAction resetAction = new ResetAction(this);

    private final EnableableAction expandAction = new ExpandTreeAction(this);

    private final EnableableAction collapseAction = new CollapseTreeAction(this);

    private final List<EnableableAction> generateActions = new ArrayList<>();

    private final List<EnableableAction> postGenerateActions = new ArrayList<>();

    private final List<EnableableAction> alwaysOnActions = new ArrayList<>();

    private final Stack<HistoryFrame> historyBehind = new Stack<>();

    private final Stack<HistoryFrame> historyAhead = new Stack<>();

    private HistoryFrame currentFrame;


    public Plugin(Project project)
    {
        this.project = project;
        generalPanel = new JPanel(new BorderLayout());

        generateActions.add(new FindDirectUsageAction(this));
        generateActions.add(new FindUsagesIncludingParentsAction(this));
        generateActions.add(new FindUsagesIncludingChildrenAction(this));
        generateActions.add(new FindUsagesIncludingParentsAndChildrenAction(this));

        postGenerateActions.add(stopFindUsagesAction);
        postGenerateActions.add(goBackAction);
        postGenerateActions.add(goForwardAction);
        postGenerateActions.add(resetAction);
        postGenerateActions.add(expandAction);
        postGenerateActions.add(collapseAction);

        alwaysOnActions.add(new ShowKeyAction(this));

        JComponent toolbarPanel = createToolbarPanel();
        generalPanel.add(toolbarPanel, BorderLayout.NORTH);

        bottomPanel = new JPanel(new BorderLayout());
        generalPanel.add(bottomPanel, BorderLayout.CENTER);

        treeView = new JBScrollPane();

        Disposable animationDisposable = Disposer.newDisposable();
        loadingPanel = new JBLoadingPanel(new FlowLayout(), animationDisposable);
        loadingPanel.startLoading();
        
        setRunning(false, false);
    }

    public void createAndRenderTree(PsiMethodImpl method, boolean includeSupers, boolean includeOverrides)
    {
        createAndRenderTreeImpl(method, includeSupers, includeOverrides);
        
        if (currentFrame != null)
        {
            historyBehind.push(currentFrame);
        }
        currentFrame = new HistoryFrame(method, includeSupers, includeOverrides);
    }

    public void finishCreatingTree(Tree tree)
    {
        this.tree = tree;
        treeView = new JBScrollPane(tree);
        setRunning(false, false);
    }

    public void stop()
    {
        progressIndicator.cancel();
        tree = null;
        currentFrame = null; // we don't want this frame ending up in the history
        setRunning(false, true);
        userCanceled = true;
    }

    public void goBack()
    {
        if (!historyBehind.isEmpty()) // just a guard; corresponding button should already be disabled if this is true
        {
            if (currentFrame != null) // could happen if Stop was pressed
            {
                historyAhead.push(currentFrame);
            }
            currentFrame = historyBehind.pop();
            createAndRenderTreeImpl(
                currentFrame.method,
                currentFrame.includeSupers,
                currentFrame.includeOverrides
            );
        }
    }

    public void goForward()
    {
        if (!historyAhead.isEmpty()) // just a guard; corresponding button should already be disabled if this is true
        {
            if (currentFrame != null) // could happen if Stop was pressed
            {
                historyBehind.push(currentFrame);
            }
            currentFrame = historyAhead.pop();
            createAndRenderTreeImpl(
                currentFrame.method,
                currentFrame.includeSupers,
                currentFrame.includeOverrides
            );
        }
    }

    public void reset()
    {
        resetHistory(); // must reset history first for setRunning() to correctly see history vars in "reset" state
        setRunning(false, true);
    }

    public void resetHistory()
    {
        currentFrame = null;
        historyBehind.clear();
        historyAhead.clear();
    }

    public void clearHistoryAhead()
    {
        historyAhead.clear();
    }

    public void expandAll()
    {
        if (tree != null)
        {
            for (int i = 0; i < tree.getRowCount(); i++)
            {
                tree.expandRow(i);
            }
        }
    }

    public void collapseAll()
    {
        if (tree != null)
        {
            for (int i = tree.getRowCount() - 1; i >= 0; i--)
            {
                tree.collapseRow(i);
            }
        }
    }

    public void showKey()
    {
        ProgressManager.getInstance().runProcess(() -> new KeyTreeGenerator(this).run(), null);
        expandAll();
    }

    public boolean userCanceled()
    {
        return userCanceled;
    }

    public JPanel getContent()
    {
        return generalPanel;
    }

    public Tree getTree()
    {
        return tree;
    }

    private JComponent createToolbarPanel()
    {
        DefaultActionGroup result = new DefaultActionGroup();

        StreamEx.of(generateActions)
            .append(postGenerateActions)
            .append(alwaysOnActions)
            .forEach(action ->
            {
                if (action.isFirstInGroup())
                {
                    result.addSeparator();
                }
                result.add(action);
            });

        ActionToolbar actionToolbar = ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.STRUCTURE_VIEW_TOOLBAR, result, true);
        
        // Suppress irrelevant build warning; see https://github.com/JetBrains/gradle-intellij-plugin/issues/777
        actionToolbar.setTargetComponent(null);
        
        return actionToolbar.getComponent();
    }

    private void createAndRenderTreeImpl(PsiMethodImpl method, boolean includeSupers, boolean includeOverrides)
    {
        setRunning(true, true);

        TreeGenerator treeGenerator = new TreeGenerator(this, project, method, includeSupers, includeOverrides);

        progressIndicator = new BackgroundableProcessIndicator(treeGenerator);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(treeGenerator, progressIndicator);
    }

    private void setRunning(boolean isRunning, boolean clearTree)
    {
        userCanceled = false;
        if (clearTree)
        {
            treeView.removeAll();
            treeView.repaint();
        }
        bottomPanel.removeAll();
        bottomPanel.add(isRunning ? loadingPanel : treeView);

        generateActions.forEach(action -> action.setEnabled(!isRunning));

        stopFindUsagesAction.setEnabled(isRunning);
        goBackAction.setEnabled(!isRunning && !historyBehind.empty());
        goForwardAction.setEnabled(!isRunning && !historyAhead.empty());
        expandAction.setEnabled(!isRunning && currentFrame != null);
        collapseAction.setEnabled(!isRunning && currentFrame != null);
        resetAction.setEnabled(!isRunning && currentFrame != null);
    }

    private record HistoryFrame(PsiMethodImpl method, boolean includeSupers, boolean includeOverrides) {}
}