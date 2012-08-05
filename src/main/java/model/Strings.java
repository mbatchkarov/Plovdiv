/*
 * Copyright (c) 2009, Miroslav Batchkarov, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products  derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * @author reseter
 */
public abstract class Strings {

    public static final String about = "Project Plovdiv v0.25 \n August 2009 \n " +
            "Miroslav Batchkarov \n University of Sussex, \n" +
            "Brighton, UK \n mmb28 (at) sussex.ac.uk";
    public static final String numberFormatError = "Please make sure you have \n " +
            "entered the correct data type! ";
    public static final String readWriteError = "Error while reading/writing file! ";
    public static final String projectName = "Project Plovdiv v0.2 ";
    //    public static final String generation = "generation";
    public static final String state = "state";
    //    public static final String steps = "steps";
    public static final String speed = "speed";
    public static final String simSleepTime = "time";
    public static final String dynamics = "dynamics";
    public static final String infected = "infected";
    public static final String next = "next";
    public static final String noGraph = "No Graph Selected! ";
    public static final String numSus = "numSusceptible";
    public static final String numInfected = "numInfected";
    public static final String numRes = "numResistant";
    public static final String help = "<html>" +
            "<h2> File: </h2> " +
            "<li>Save to/ load from Pajek file" +
            "<li>Generate a random/scale-free/small-world graph" +
            "<h2>View" +
            "<li>Show the detailed statistics window " +
            "<li> Label each node with the disease generation it got infected in" +
            "<h2>Change layout" +
            "<li>Allows you to select one of the wide variety of predefined layouts" +
            "<h2>Change layout" +
            "<li> Run until" +
            "Lets you specify settings for the simulation and runs for the specified steps" +
            "<li> Stop" +
            "Terminates the simulation" +
            "<li> Infect nodes" +
            "Lets you infect a specified number of vertices at random" +
            "<li> Set all to susceptible" +
            "Sets all vertices back to susceptible state" +
            "<h1> Using the mouse </h1>" +
            "<h3>All Modes:</h3>" +
            "<ul>" +
            "<li>Right-click for context-sensitive menu" +
            "<li>Mousewheel zooms in and out" +
            "</ul>" +
            "<h3>Editing Mode:</h3>" +
            "<ul>" +
            "<li>Left-click an empty area to create a new Vertex" +
            "<li>Left-click on a Vertex and drag to another Vertex to create an Undirected Edge" +
            "<li>Shift+Left-click on a Vertex and drag to another Vertex to create a Directed Edge" +
            "</ul>" +
            "<h3>Picking Mode:</h3>" +
            "<ul>" +
            "<li>Mouse1 on a Vertex selects the vertex" +
            "<li>Mouse1 elsewhere unselects all Vertices" +
            "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection" +
            "<li>Mouse1+drag on a Vertex moves all selected Vertices" +
            "<li>Mouse1+drag elsewhere selects Vertices in a region" +
            "<li>Mouse1+Shift+drag adds selection of Vertices in a new region" +
            "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it" +
            "</ul>" +
            "<h3>Transforming Mode:</h3>" +
            "<ul>" +
            "<li>Mouse1+drag pans the graph" +
            "<li>Mouse1+Shift+drag rotates the graph" +
            "<li>Mouse1+CTRL(or Command)+drag shears the graph" +
            "</ul>" +
            "<h3>Annotation Mode:</h3>" +
            "<ul>" +
            "<li>Mouse1+drag draws a rectangle as an annotation" +
            "<li>Mouse1+Shift draws an ellipse as an annotation" +
            "<li>Shift+ click deletes the annotation" +
            "<li>Mouse3 shows a popup to input text, which will become a text " +
            "annotation on the graph at the mouse location" +
            "</ul>" +
            "</html>";
}

