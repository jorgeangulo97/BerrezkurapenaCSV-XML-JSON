/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exekutagarri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.Score;
import model.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import view.Jolasa;

/**
 *
 * @author Jorge
 */
public class MenuExekutagarria {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        boolean irten = false;
        int aukera; //Guardaremos la opcion del usuario
        Jolasa jolasa = null;

        while (!irten) {
            System.out.println("\n      BERRESKURAPENA CSV-XML-JSON          ");
            System.out.println("+-------------------------------------------+");
            System.out.println("|  1. Irakurri Json eta Gorde CSV moduan    |");
            System.out.println("|  2. Irakurri XML eta Gorde JSON moduan    |");
            System.out.println("|  3. Irakurri CSV eta Gorde XML moduan     |");
            System.out.println("|  4. Jolasa                                |");
            System.out.println("|  5. Irten                                 |");
            System.out.println("+-------------------------------------------+");

            System.out.println("Sartu zenbaki bat aukeratzeko: ");
            aukera = readInt(sc);

            ArrayList<Student> students;
            
            switch (aukera) {
                case 1:
                    students = loadJson("students.json");
                    saveCSV(students, "studentsCSV.csv");
                    break;
                case 2:
                    students = loadXml("studentsxml.xml");
                    saveJson(students, "studentsJSON.json");
                    break;
                case 3:
                    students = loadCsv("students.csv");
                    saveXML(students, "studentsXML.xml");
                    break;
                case 4:
                    System.out.print("Zein fitxategia erabili nahi duzu (studentsJSON.json/studentsCSV.csv/studentsXML.xml): ");
                    String path = sc.next();
                    students = jolasaHasteko(path);
                    jolasa = new Jolasa(students);
                    jolasa.setVisible(true);
                    jolasa.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            
                    while (true) {
                        Thread.sleep(100);
                        jolasa.repaint();
                    }                    
                    
                case 5:
                    irten = true;
                    break;
                default:
                    System.out.println("Zenbaki bat sartu behar duzu 1-5");
            }
        }
        
        sc.close();
    }
    
    private static int readInt(Scanner sc) {
        int value = -1;
        try {
            value = sc.nextInt();
        } catch (Exception e) {
            sc.nextLine(); // Limpio la linea que estaba mal (Limpiar el buffer)
            System.out.println("Zenbaki bat jarri behar duzu eta positiboa izan behar da.");
        }
        return value;
    }
    
    public static ArrayList<Student> loadCsv(String path) throws FileNotFoundException{ 
        
        BufferedReader br;
        String linea;
        ArrayList<Student> students_final = new ArrayList<>();

        br = new BufferedReader(new FileReader(path));

        try {
            br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] students = linea.split(";");
                
                int id = Integer.parseInt(students[0]);
                String name = students[1];
                String scoresJsonString = students[2].replace("\"\"", "\"");
                scoresJsonString = scoresJsonString.substring(1, scoresJsonString.length() - 1);
                
                // Scores tiene un JSON asi que hay que leerlo y parsearlo
                JsonReader jsonReader = Json.createReader(new StringReader(scoresJsonString));
                JsonStructure jsonst = jsonReader.read();
                
                JsonArray jsonArray = jsonst.asJsonArray();
                
                ArrayList<Score> scores = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject currentObjectScore = jsonArray.getJsonObject(i);
                    double scoreValue = currentObjectScore.getJsonNumber("score").doubleValue();
                    String type = currentObjectScore.getString("type");
                
                    Score score = new Score(scoreValue, type);
                    scores.add(score);
                }
                
                Student student = new Student(id, name, scores);
                students_final.add(student);
            }
        } catch (IOException ex) {
            Logger.getLogger(MenuExekutagarria.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Ondo joan da!");
        
        return students_final;
    }
    
    public static ArrayList<Student> loadXml(String path) throws FileNotFoundException {
        
        ArrayList<Student> students_final = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(path));
            
            NodeList students = document.getElementsByTagName("Student");
            for (int i = 0; i < students.getLength(); i++) {
                Node student = students.item(i);
                Element elemStudent = (Element) student;
                int id = Integer.parseInt(elemStudent.getElementsByTagName("_id").item(0).getTextContent());
                String name = elemStudent.getElementsByTagName("name").item(0).getTextContent();    
                
                
                ArrayList<Score> scores = new ArrayList<>();
                
                Node scoresNode = elemStudent.getElementsByTagName("scores").item(0);
                Element elemScoresNode = (Element) scoresNode;
                
                NodeList typesNodeList = elemScoresNode.getElementsByTagName("type");
                NodeList scoresNodeList = elemScoresNode.getElementsByTagName("score");
                
                for (int j = 0; j < typesNodeList.getLength(); j++) {
                    Node typeNode = typesNodeList.item(j);
                    Node scoreNode = scoresNodeList.item(j);
                    
                    Score score = new Score(Double.valueOf(scoreNode.getTextContent()), typeNode.getTextContent());
                    scores.add(score);
                }
                
                double avgScore = Double.parseDouble(elemStudent.getElementsByTagName("avgScore").item(0).getTextContent());
                
                Student currentStudent = new Student(id, name, scores, avgScore);
                students_final.add(currentStudent);
            }
            
        } catch(ParserConfigurationException | SAXException | IOException ex) {
            System.err.println("Errore bat gertatu da.");
        }
        
        return students_final;
    }
    
    public static ArrayList<Student> loadJson(String path) throws FileNotFoundException{
        JsonReader reader = Json.createReader(new FileReader(path));
        JsonStructure jsonst = reader.read();
        JsonArray jsonArray = jsonst.asJsonArray();

        ArrayList<Student> students = new ArrayList<>();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        
        for (int i = 0; i < jsonArray.size(); i++) {
            jab.add(jsonst.asJsonArray().get(i));
            
            JsonObject currentObjectStudent = jsonArray.getJsonObject(i);
            
            int id = currentObjectStudent.getInt("_id");
            String name = currentObjectStudent.getString("name");
            JsonArray scoresObjectCurrentStudent = currentObjectStudent.getJsonArray("scores");
            
            ArrayList<Score> scores = new ArrayList<>();
            
            for (int j = 0; j < scoresObjectCurrentStudent.size(); j++) {
                JsonObject currentObjectScore = scoresObjectCurrentStudent.getJsonObject(j);
                double scoreValue = currentObjectScore.getJsonNumber("score").doubleValue();
                String type = currentObjectScore.getString("type");
                
                Score score = new Score(scoreValue, type);
                scores.add(score);
            }
           
            double avgScore = currentObjectStudent.getJsonNumber("avgScore").doubleValue();

            Student currentStudent = new Student(id, name, scores, avgScore);
            students.add(currentStudent);
        }

        return students;
    }
    
    public static void saveCSV(ArrayList<Student> students, String path) {

        try { 
            FileWriter writer = new FileWriter(path);
            JsonArrayBuilder jab = Json.createArrayBuilder();
            writer.write("_id;name;scores;avgScore\n");
            for (Student student : students) {
                int id = student.getId();
                String name = student.getName();
                Double avgScore = student.getAvgScore();
                
                for (Score score : student.getScores()) {
                    JsonObjectBuilder scoreJob = Json.createObjectBuilder();
                    scoreJob.add("score", score.getScore());
                    scoreJob.add("type", score.getType());
                    
                    jab.add(scoreJob);
                }

                JsonArray model = jab.build();
                

                String linea = id + ";" + name + ";\"" + (model + "").replace("\"", "\"\"") + "\";" +  avgScore + "\n";
                writer.write(linea);
            }
        writer.close();
        } catch (IOException ex) {
            System.out.println("Errore bat gertatu da Csv-ra pasatzerakoan");
        }
    }
    
    public static void saveXML(ArrayList<Student> students, String path) {
        
            try{
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = (Document) documentBuilder.newDocument();

                Element root = document.createElement("Students");
                document.appendChild(root);
                
                for (Student student : students) {

                    Element elemStudent = document.createElement("Student");

                    Element elemId = document.createElement("_id");
                    elemId.appendChild(document.createTextNode(student.getId() + ""));
                    elemStudent.appendChild(elemId);

                    Element elemName = document.createElement("name");
                    elemName.appendChild(document.createTextNode(student.getName()));
                    elemStudent.appendChild(elemName);

                    Element elemScores = document.createElement("scores");
                    
                    for (Score score : student.getScores()) {
                        Element elemType = document.createElement("type");
                        elemType.appendChild(document.createTextNode(score.getType()));
                        Element elemScore = document.createElement("score");
                        elemScore.appendChild(document.createTextNode(score.getScore() + ""));
                        elemScores.appendChild(elemType);
                        elemScores.appendChild(elemScore);
                    }
                    elemStudent.appendChild(elemScores);
                    
                    Element elemAvgScore = document.createElement("avgScore");
                    elemAvgScore.appendChild(document.createTextNode(student.getAvgScore() + ""));
                    elemStudent.appendChild(elemAvgScore);
                    
                    root.appendChild(elemStudent);
                }
                

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(new File(path));
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(domSource, streamResult);
                
            } catch (TransformerException | ParserConfigurationException ex) {
                System.err.println("Errore bat gertatu XML gordetzerakoan");
            }
    }

    public static void saveJson(ArrayList<Student> students, String path){
        
        FileWriter fw = null;

        try {
            JsonArrayBuilder jab = Json.createArrayBuilder();
            for (Student student : students) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("_id", student.getId());
                job.add("name", student.getName());
                job.add("avgScore", student.getAvgScore());
                
                JsonArrayBuilder scoreJab = Json.createArrayBuilder();
                for (Score score : student.getScores()) {
                    JsonObjectBuilder scoreJob = Json.createObjectBuilder();
                    scoreJob.add("score", score.getScore());
                    scoreJob.add("type", score.getType());
                    
                    scoreJab.add(scoreJob);
                }
                
                job.add("scores", scoreJab);
                
                jab.add(job);
            }
            
            JsonArray model = jab.build();
            fw = new FileWriter(path);
            JsonWriter jsonwriter = Json.createWriter(fw);
            jsonwriter.writeArray(model);
            jsonwriter.close();
        } catch (IOException ex) {
            System.err.println("Errore bat gertatu da");
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                System.err.println("Errore bat gertatu da");
            }
        }
    }
    
    public static ArrayList<Student> jolasaHasteko(String path) throws FileNotFoundException{
        
        ArrayList<Student> students = null;
        if (path.endsWith(".csv")) {
            students = loadCsv(path);
        } else if (path.endsWith(".json")) {
            students = loadJson(path);
        } else if (path.endsWith(".xml")) {
            students = loadXml(path);
        } else {
            System.err.println("Fitxero horien luzapena ez da baliogarria");
        }
        return students;
    }
}
