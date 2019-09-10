package GraphicTree;

import EngineRunner.ModuleTwo;
import GraphicTree.layout.CommitTreeLayout;
import GraphicTree.node.CommitNode;

import Objects.Commit.Commit;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.HashMap;


public class GraphicCommitNodeMaker {
    public static HashMap<String, ICell> cellMap = new HashMap<>();

    public static void createGraphicTree(ScrollPane scrollPane) {
        Graph tree = new Graph();
        ArrayList<Commit> commitLst = ModuleTwo.getActiveRepo().getCommits();
        GraphicCommitNodeMaker.createCommits(tree, commitLst);
        PannableCanvas canvas = tree.getCanvas();
        //canvas.setPrefWidth(100);
        //canvas.setPrefHeight(100);
        scrollPane.setContent(canvas);

        Platform.runLater(() -> {
            tree.getUseViewportGestures().set(false);
            tree.getUseNodeGestures().set(false);
        });
    }

    public static void createCommits(Graph graph, ArrayList<Commit> commitLst) {

        final Model model = graph.getModel();

        graph.beginUpdate();
        ICell c;

        for (Commit commit : commitLst) {
            c = new CommitNode(commit.getDateAndTime().getDate(), commit.getNameOfModifier(),
                    commit.getCommitPurposeMSG(),commit,ModuleTwo.isPointedCommit(commit));
            cellMap.put(commit.getSha1(), c);
            model.addCell(c);
        }
        for (Commit commit : commitLst) {
            if (cellMap.containsKey(commit.getPreviousCommitSha1())) {
                final Edge edge = new Edge(cellMap.get(commit.getSha1()), cellMap.get(commit.getPreviousCommitSha1()));
                model.addEdge(edge);
            }
        }
        graph.endUpdate();
        graph.layout(new CommitTreeLayout(cellMap,commitLst.get(0),commitLst));

        //graph.getModel().getAllCells().forEach();
    }
}
