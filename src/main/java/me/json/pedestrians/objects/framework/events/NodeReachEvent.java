package me.json.pedestrians.objects.framework.events;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Gets called before determining new targets
 */
public class NodeReachEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Pedestrian pedestrian;
    private final Node reachedNode;

    private @Nullable Node firstForcedTargetNode;
    private @Nullable Node secondForcedTargetNode;

    public NodeReachEvent(Pedestrian pedestrian, Node node) {
        super(true);
        this.pedestrian = pedestrian;
        this.reachedNode = node;
    }

    public Pedestrian getPedestrian() {
        return this.pedestrian;
    }

    public Node getReachedNode() {
        return this.reachedNode;
    }

    public boolean containsTag(String tag) {
        return this.reachedNode.tags().contains(tag);
    }

    @Nullable
    public String getTagByPrefix(String tagPrefix) {

        String result = null;

        for (String tag : this.reachedNode.tags()) {

            if(tag.startsWith(tagPrefix+"_")) {
                result = tag;
            }

        }

        return result;
    }

    @Nullable
    public String getTagValueByPrefix(String tagPrefix) {

        String tag = getTagByPrefix(tagPrefix);
        if(tag != null) {
            return tag.split("_")[1];
        } else {
            return null;
        }

    }

    @Nullable
    public Node getFirstForcedTargetNode() {
        return this.firstForcedTargetNode;
    }

    @Nullable
    public Node getSecondForcedTargetNode() {
        return this.secondForcedTargetNode;
    }

    public void setFirstForcedTargetNode(@Nullable Node firstForcedTargetNode) {
        this.firstForcedTargetNode = firstForcedTargetNode;
    }

    public void setSecondForcedTargetNode(@Nullable Node secondForcedTargetNode) {
        this.secondForcedTargetNode = secondForcedTargetNode;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
