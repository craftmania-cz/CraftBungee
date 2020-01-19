package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.managers.notes.Note;
import cz.wake.craftbungee.managers.notes.NotePlayer;
import cz.wake.craftbungee.utils.TextComponentBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.StringJoiner;

public class Note_command extends Command {

    private Main pl;

    public Note_command(Main pl) {
        super("note", "craftbungee.notes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args.length >= 2) {
            String player = args[0];
            String operation = args[1];
            NotePlayer notePlayer = Main.getNoteManager().getNotePlayer(player);
            switch (operation.toLowerCase()) {
                case "add":
                    if (args.length >= 3) {
                        StringJoiner noteBuilder = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            noteBuilder.add(args[i]);
                        }
                        String stringNote = noteBuilder.toString();

                        Note note = new Note(player, sender.getName(), stringNote);
                        notePlayer.addNote(note);
                        note.addToCache();

                        sender.sendMessage("§e§l[*] §ePoznámka pro hráče '" + player + "' byla přidána (ID: " + note.getId() + ").");
                        break;
                    } else {
                        sendHelp(sender);
                        break;
                    }
                case "remove":
                    if (args.length >= 3) {
                        if (!notePlayer.hasNotes()) {
                            sender.sendMessage("§c§l[!] §cTento hráč nemá poznámky.");
                            return;
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sendHelp(sender);
                            break;
                        }
                        try {
                            if (!notePlayer.hasNoteWithID(id)) {
                                sender.sendMessage("§c§l[!] §cHráč '" + player + "' nemá poznámku s ID " + id + ".");
                                break;
                            }
                            notePlayer.removeNote(id);
                            sender.sendMessage("§e§l[*] §ePoznámka s ID " + id + " hráče '" + player + "' byla vymazána.");
                            break;
                        } catch (Exception e) {
                            sender.sendMessage("§c§l[!] §cNastala chyba při vymazávaní poznámky s ID " + id + ".");
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        sendHelp(sender);
                        break;
                    }
                case "clear":
                    if (!notePlayer.hasNotes()) {
                        sender.sendMessage("§c§l[!] §cTento hráč nemá poznámky.");
                        return;
                    }
                    notePlayer.clearNotes();
                    sender.sendMessage("§e§l[*] §ePoznámka hráče '" + player + "' byly vymazány.");
                    break;
                case "list":
                    if (!notePlayer.hasNotes()) {
                        sender.sendMessage("§c§l[!] §cTento hráč nemá poznámky.");
                        return;
                    }
                    sender.sendMessage("§ePoznámky hráče: " + player);
                    for (Note note : notePlayer.getNotes()) {
                        TextComponent noteComponent = new TextComponentBuilder("§8[ID: " + note.getId() + "] §f'" + note.getNote() + "'").setTooltip("§7Admin: §f" + note.getAdmin() + "\n§7Datum: §f" + note.getFormattedDatetime()).getComponent();
                        TextComponent deleteComponent = new TextComponentBuilder(" §c[ZMAZAT]").setTooltip("Vymaže poznámku s ID " + note.getId()).setPerformedCommand("note " + player + " remove " + note.getId()).getComponent();
                        noteComponent.addExtra(deleteComponent);
                        sender.sendMessage(noteComponent);
                    }
                    break;
                default:
                    sendHelp(sender);
                    break;
            }
        } else {
            sendHelp(sender);
            return;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§c§l[!] §cNesprávný formát! Použij /note (nick) [add/remove/clear/list] [note]");
    }

    //Todo: autocomplete
}
