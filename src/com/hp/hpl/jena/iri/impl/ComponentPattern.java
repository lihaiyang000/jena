/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.iri.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.ViolationCodes;

public class ComponentPattern implements ViolationCodes {
    final Pattern pattern;

    final GroupAction actions[];

    static final ArrayList allPatterns = new ArrayList();

    ComponentPattern(String p) {
        ComponentPatternParser parser = new ComponentPatternParser(p);
        pattern = parser.get();
        actions = parser.actions();
//        System.err.println(allPatterns.size() + ": " + p + " ==> "
//                + pattern.toString());
        allPatterns.add(pattern);

    }

    static public void main(String args[]) throws IOException {
        IRIFactory.iriImplementation();
        LineNumberReader in = new LineNumberReader(new InputStreamReader(
                System.in));
        String xx = "[(](?![?])|[(][?]|[)]|\\[|\\]|[@][{]|[)][}]";
        Pattern p = Pattern.compile("(?=" + xx + ")|(?<=" + xx + ")");

        while (true) {
            String s = in.readLine().trim();
            if (s.equals("quit"))
                return;
            try {
                int i = Integer.parseInt(s);
                p = (Pattern) allPatterns.get(i);
                continue;
            } catch (Exception e) {

            }
            if (s.startsWith("$")) {
                try {
                p = Pattern.compile(s.substring(1));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            Matcher m = p.matcher(s);
            m.find();
            System.out.println("Match from: " + m.start() + " to " + m.end()
                    + "/" + s.length());
            for (int g = 1; g <= m.groupCount(); g++)
                System.out.println("Group " + g + " from: " + m.start(g)
                        + " to " + m.end(g) + "/" + s.length());
        }

    }

    public void analyse(Parser parser, int range) {
        Matcher m = pattern.matcher(parser.get(range));
        if (!m.matches()) {
            parser.recordError(range, SCHEME_PATTERN_MATCH_FAILED);
            return;
        }
        for (int g = 1; g <= m.groupCount(); g++)
            if (m.start(g) != -1)
                actions[g].check(m, parser, range);

    }
}

/*
 * (c) Copyright 2005 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

