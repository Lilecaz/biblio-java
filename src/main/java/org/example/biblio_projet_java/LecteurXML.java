package org.example.biblio_projet_java;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class LecteurXML {

    public static List<Livre> lireLivres(File fichierXML) throws Exception {

        List<Livre> livres = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(fichierXML);

        NodeList nodeList = document.getElementsByTagName("livre");
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                Livre livre = new Livre();
                livre.setTitre(element.getElementsByTagName("titre").item(0).getTextContent());
                livre.setPresentation(element.getElementsByTagName("presentation").item(0).getTextContent());
                livre.setParution(Integer.parseInt(element.getElementsByTagName("parution").item(0).getTextContent()));
                livre.setColonne(Integer.parseInt(element.getElementsByTagName("colonne").item(0).getTextContent()));
                livre.setRangee(Integer.parseInt(element.getElementsByTagName("rangee").item(0).getTextContent()));

                livres.add(livre);
            }
        }

        return livres;
    }

}