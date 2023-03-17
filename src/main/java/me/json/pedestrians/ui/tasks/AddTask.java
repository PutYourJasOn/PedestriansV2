package me.json.pedestrians.ui.tasks;

import com.comphenix.protocol.wrappers.ComponentParser;
import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.ui.EditorView;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class AddTask implements ITask {

    private EditorView editorView;
    private boolean preciseMode = false;

    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
        editorView.player().sendMessage("TEST");
    }

    @Override
    public void onRightClickNode(Node node) {

    }

    @Override
    public void onLeftClickNode(Node node) {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onLeftClick() {
        preciseMode = !preciseMode;
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.PRECISE_MODE_TOGGLE,preciseMode)));
    }

    @Override
    public void onScroll(int scrollDirection) {

    }

    @Override
    public void render() {

    }

}
