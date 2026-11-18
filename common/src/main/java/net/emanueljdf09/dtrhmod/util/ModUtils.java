package net.emanueljdf09.dtrhmod.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class ModUtils {

    public static List<String> splitIntoPages(String rawText, int widthPx, int linesPerPage) {
        Font renderer = Minecraft.getInstance().font;
        List<String> lines = new ArrayList<>();
        String[] sourceLines = rawText.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);

        for (String source : sourceLines) {
            String trimmed = source.trim();
            if (trimmed.isEmpty()) {
                if (!lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty()) {
                    lines.add("");
                }
                continue;
            }
            wrapInto(lines, trimmed, renderer, widthPx);
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }

        List<String> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();
        for (String line : lines) {
            if (page.isEmpty() && line.isEmpty()) continue;
            page.add(line);
            if (page.size() >= linesPerPage) {
                pages.add(joinPage(page));
                page.clear();
            }
        }
        if (!page.isEmpty()) {
            pages.add(joinPage(page));
        }

        if (pages.isEmpty()) {
            pages.add("");
        }

        return pages;
    }

    private static String joinPage(List<String> page) {
        List<String> copy = new ArrayList<>(page);
        while (!copy.isEmpty() && copy.get(copy.size() - 1).isEmpty()) {
            copy.remove(copy.size() - 1);
        }
        return String.join("\n", copy);
    }

    private static void wrapInto(List<String> out, String text, Font renderer, int widthPx) {
        StringBuilder current = new StringBuilder();
        for (String word : text.split("\\s+")) {
            while (renderer.width(word) > widthPx) {
                if (current.length() > 0) {
                    out.add(current.toString());
                    current.setLength(0);
                }
                int cut = 1;
                while (cut < word.length() && renderer.width(word.substring(0, cut + 1)) <= widthPx) {
                    cut++;
                }
                out.add(word.substring(0, cut));
                word = word.substring(cut);
            }
            if (word.isEmpty()) continue;

            String candidate = current.length() == 0 ? word : current + " " + word;
            if (renderer.width(candidate) <= widthPx) {
                current.setLength(0);
                current.append(candidate);
            } else {
                out.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        if (current.length() > 0) {
            out.add(current.toString());
        }
    }
}