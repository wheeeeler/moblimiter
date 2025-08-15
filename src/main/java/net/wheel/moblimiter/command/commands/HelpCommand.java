package net.wheel.moblimiter.command.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import net.wheel.moblimiter.util.MLColor;
import net.wheel.moblimiter.util.Messages;

public final class HelpCommand {

    private static final int PER_PAGE = 9;

    private record HelpLine(String command, String description) {
    }

    public static void regCMD(LiteralArgumentBuilder<CommandSourceStack> root) {

        root.then(literal("help")
                .executes(ctx -> showAll(ctx.getSource(), 1))
                .then(argument("Page or Command", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String arg = StringArgumentType.getString(ctx, "Page or Command").trim();
                            if (arg.matches("\\d+")) {
                                int page = Math.max(1, Integer.parseInt(arg));
                                return showAll(ctx.getSource(), page);
                            }
                            return showFiltered(ctx.getSource(), arg);
                        })));
    }

    private static int showAll(CommandSourceStack src, int page) {
        List<HelpLine> lines = entriesSorted();

        int totalPages = Math.max(1, (int) Math.ceil(lines.size() / (double) PER_PAGE));
        if (page > totalPages) {
            final Component noPage = MLColor.parse(Messages.text("help.nopage", page, totalPages));
            src.sendSuccess(() -> noPage, false);
            return 1;
        }

        int from = (page - 1) * PER_PAGE;
        int to = Math.min(lines.size(), from + PER_PAGE);

        final Component header = MLColor.parse(Messages.text("help.header", page, totalPages));
        src.sendSuccess(() -> header, false);

        for (int i = from; i < to; i++) {
            final MutableComponent entry = renderEntry(lines.get(i));
            src.sendSuccess(() -> entry, false);
        }

        final Component footer = MLColor.parse(Messages.text("help.footer.page", page, totalPages));
        src.sendSuccess(() -> footer, false);
        return 1;
    }

    private static int showFiltered(CommandSourceStack src, String queryRaw) {
        final String query = queryRaw.toLowerCase(Locale.ROOT);

        List<HelpLine> all = entriesSorted();
        List<HelpLine> match = new ArrayList<>();
        for (HelpLine hl : all) {
            String c = hl.command.toLowerCase(Locale.ROOT);
            if (c.contains(query)) {
                match.add(hl);
            }
        }

        if (match.isEmpty()) {
            final Component none = MLColor.parse(Messages.text("help.filter.none", queryRaw));
            src.sendSuccess(() -> none, false);
            return 1;
        }

        final Component header = MLColor.parse(Messages.text("help.filter.header", queryRaw, match.size()));
        src.sendSuccess(() -> header, false);

        for (HelpLine hl : match) {
            final MutableComponent entry = renderEntry(hl);
            src.sendSuccess(() -> entry, false);
        }

        return 1;
    }

    private static MutableComponent renderEntry(HelpLine hl) {
        String pretty = Messages.text("help.entry.line", hl.command, hl.description);
        MutableComponent line = Component.empty().append(MLColor.parse(pretty));

        line.withStyle(s -> s.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                MLColor.parse(Messages.text("help.entry.hover", hl.command, hl.description)))).withUnderlined(false)
                .withColor(ChatFormatting.WHITE));

        return line;
    }

    private static List<HelpLine> entriesSorted() {
        List<HelpLine> list = new ArrayList<>();

        list.add(new HelpLine("ml author", Messages.text("help.author")));
        list.add(new HelpLine("ml feedback enable|disable", Messages.text("help.feedback")));
        list.add(new HelpLine("ml forceclear", Messages.text("help.ac.force")));
        list.add(new HelpLine("ml listentities <all|world>", Messages.text("help.listentities")));
        list.add(new HelpLine("ml reload", Messages.text("help.reload")));
        list.add(new HelpLine("ml status", Messages.text("help.status")));
        list.add(new HelpLine("ml update whitelist add <modid|modid:entity|entity>", Messages.text("help.wl.add")));
        list.add(new HelpLine("ml update whitelist list", Messages.text("help.wl.list")));
        list.add(new HelpLine("ml update whitelist remove <modid|modid:entity|entity>",
                Messages.text("help.wl.remove")));
        list.add(new HelpLine("ml update entitylimit enable|disable", Messages.text("help.el.toggle")));
        list.add(new HelpLine("ml update entitylimit limit <value>", Messages.text("help.el.limit")));
        list.add(new HelpLine("ml update entitylimit list strictlimit", Messages.text("help.el.list")));
        list.add(new HelpLine("ml update entitylimit remove strictlimit <entry>", Messages.text("help.el.remove")));
        list.add(new HelpLine("ml update entitylimit strictlimit <entry> <value>",
                Messages.text("help.el.strictlimit")));
        list.add(new HelpLine("ml update autoclear enable|disable", Messages.text("help.ac.toggle")));
        list.add(new HelpLine("ml update autoclear limit <value>", Messages.text("help.ac.limit")));
        list.add(new HelpLine("ml update autoclear list strictlimit", Messages.text("help.ac.list")));
        list.add(new HelpLine("ml update autoclear remove strictlimit <entry>", Messages.text("help.ac.remove")));
        list.add(new HelpLine("ml update autoclear timer <seconds>", Messages.text("help.ac.timer")));
        list.add(new HelpLine("ml update autoclear strictlimit <entry> <value>", Messages.text("help.ac.strictlimit")));

        list.sort(Comparator.comparing(hl -> hl.command.toLowerCase(Locale.ROOT)));
        return list;
    }
}
