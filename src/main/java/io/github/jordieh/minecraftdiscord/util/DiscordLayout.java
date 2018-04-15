///*
// *     This file is part of MinecraftDiscord.
// *
// *     MinecraftDiscord is free software: you can redistribute it and/or modify
// *     it under the terms of the GNU Lesser General Public License as published by
// *     the Free Software Foundation, either version 3 of the License, or
// *     (at your option) any later version.
// *
// *     MinecraftDiscord is distributed in the hope that it will be useful,
// *     but WITHOUT ANY WARRANTY; without even the implied warranty of
// *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *     GNU Lesser General Public License for more details.
// *
// *     You should have received a copy of the GNU Lesser General Public License
// *     along with MinecraftDiscord.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package io.github.jordieh.minecraftdiscord.util;
//
//import org.apache.log4j.HTMLLayout;
//import org.apache.log4j.Layout;
//import org.apache.log4j.helpers.Transform;
//import org.apache.log4j.spi.LoggingEvent;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//// https://stackoverflow.com/questions/20795373
//public class DiscordLayout extends HTMLLayout {
//
//    private StringBuffer sbuf = new StringBuffer(super.BUF_SIZE);
//
//    @Override
//    public String format(LoggingEvent event) {
//        if (sbuf.capacity() > super.MAX_CAPACITY) {
//            sbuf = new StringBuffer(super.BUF_SIZE);
//        } else {
//            sbuf.setLength(0);
//        }
//
//        sbuf.append(Layout.LINE_SEP);
//        sbuf.append("<tr>");
//        sbuf.append(Layout.LINE_SEP);
//
//        // Current timestamp
//        sbuf.append("<td>");
//        sbuf.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
//        sbuf.append("</td>");
//        sbuf.append(Layout.LINE_SEP);
//
//        // Name of the used Thread
//        sbuf.append("<td>");
//        sbuf.append(Transform.escapeTags(event.getThreadName()));
//        sbuf.append("</td>");
//        sbuf.append(Layout.LINE_SEP);
//
//        // Name of the Category
//        sbuf.append("<td>");
//        sbuf.append(Transform.escapeTags(event.getLocationInformation().getClassName()));
//        sbuf.append("</td>");
//        sbuf.append(Layout.LINE_SEP);
//
//        // String version of the level name
//        String level = Transform.escapeTags(String.valueOf(event.getLevel()));
//        sbuf.append("<td class=\"");
//        sbuf.append(level);
//        sbuf.append("\">");
//        sbuf.append(level);
//        sbuf.append("</td>");
//        sbuf.append(Layout.LINE_SEP);
//
//        sbuf.append("<td>");
//        sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
//        sbuf.append("</td></tr>");
//        sbuf.append(Layout.LINE_SEP);
//
//        String[] s = event.getThrowableStrRep(); // Handle exceptions
//        if (s != null) {
//            sbuf.append("<tr><td class=\"stacktrace\" colspan=\"100\">");
//            appendThrowableAsHTML(s, sbuf);
//            sbuf.append("</td></tr>");
//            sbuf.append(Layout.LINE_SEP);
//        }
//
//        return sbuf.toString();
//    }
//
//    @Override
//    public String getHeader() {
//        return "</tbody></table><hr>" +
//                "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://assets-cdn.github.com/favicon.ico\">" +
//                "<style type=\"text/css\">" +
//                "body {font-family: \"arial\", Courier, monospace;font-size: smaller;}" +
//                "th {background: #336699;color: #FFFFFF}" +
//                "table {table-layout: auto;width: 100%;border-collapse: collapse;text-align: left;}" +
//                ".absorbing-column{width: 100%}th, td {white-space: nowrap;border: 1px solid black;padding: 5px;}" +
//                "th, td {padding: 5px;}" +
//                ".TRACE {font-style: italic;}" +
//                ".DEBUG {font-style: italic;}" +
//                ".INFO {color: #00AAAA;}" +
//                ".WARN {color: #FFAA00;}" +
//                ".stacktrace {background-color: #993300; color:white; font-size: x-small;}" +
//                ".ERROR {color: #AA0000;}</style>" +
//                "<table><thead><tr><th>Date</th><th>Thread</th><th>Category</th><th>Level</th><th class=\"absorbing-column\">Message</th></tr></thead><tbody>";
//    }
//
//    private void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
//        if(s != null) {
//            int len = s.length;
//            if(len == 0)
//                return;
//            sbuf.append(Transform.escapeTags(s[0]));
//            sbuf.append(Layout.LINE_SEP);
//            for(int i = 1; i < len; i++) {
//                sbuf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;");
//                sbuf.append(Transform.escapeTags(s[i]));
//                sbuf.append(Layout.LINE_SEP);
//            }
//        }
//    }
//}
