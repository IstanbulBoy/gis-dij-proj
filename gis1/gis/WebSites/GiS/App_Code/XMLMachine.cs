using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Xml;
using System.Xml.Linq;
using System.IO;
using System.Collections;
using System.Xml.Serialization;

using QuickGraph;
using QuickGraph.Serialization;
using QuickGraph.Serialization.DirectedGraphML;
using QuickGraph.Algorithms;


/// <summary>
/// Summary description for XMLMachine
/// </summary>
public class XMLMachine
{
    const double INF = 2147483647;

    public TSPAdjacencyGraph ReadFromGraphML(XmlReader xreader)
    {
        var g = new TSPAdjacencyGraph();

        g.DeserializeFromGraphML(xreader, id => new TSPNode(id), (source, target, id) => new TSPEdge(source, target));

        return g;
    }

    public void WriteGraphMLToStream(TSPAdjacencyGraph g, Stream stream)
    {
        var xwriter = XmlWriter.Create(stream);
        g.SerializeToGraphML(xwriter, AlgorithmExtensions.GetVertexIdentity(g), AlgorithmExtensions.GetEdgeIdentity(g));

        xwriter.Flush();
    }

    public TSPAdjacencyGraph GenerateGraphFromMatrix(double[,] matrix)
    {
        int nodesCount = (int)Math.Sqrt(matrix.Length);

        TSPAdjacencyGraph g = new TSPAdjacencyGraph();

        g.size = nodesCount;

        for (int i = 0; i < nodesCount; i++)
        {
            g.AddVertex(new TSPNode(i));
        }

        for (int i = 0; i < nodesCount; i++)
        {
            for (int j = 0; j < nodesCount; j++)
            {
                if (i != j)
                    g.AddEdge(new TSPEdge(g.Vertices.Single(m => m.id == i.ToString()), g.Vertices.Single(m => m.id == j.ToString()))
                    {
                        weight = matrix[i, j]
                    });
            }
        }

        return g;
    }

    public double[,] GenerateMatrixFromGraphML(TSPAdjacencyGraph g)
    {
        double[,] matrix = null;

        matrix = new double[g.VertexCount, g.VertexCount];

        for (int j = 0; j < g.VertexCount; j++)
        {
            for (int k = 0; k < g.VertexCount; k++)
            {
                matrix[j, k] = INF;
            }
        }

        Hashtable nodeHashtable = new Hashtable();

        int i = 0;
        foreach (TSPNode node in g.Vertices)
            nodeHashtable[node.id] = i++;

        if (g.IsDirected)
        {
            foreach (TSPEdge edge in g.Edges)
                matrix[(int)nodeHashtable[edge.Source.id], (int)nodeHashtable[edge.Target.id]] = edge.weight;
        }
        else
        {
            foreach (TSPEdge edge in g.Edges)
            {
                matrix[(int)nodeHashtable[edge.Source.id], (int)nodeHashtable[edge.Target.id]] = edge.weight;
                matrix[(int)nodeHashtable[edge.Target.id], (int)nodeHashtable[edge.Source.id]] = edge.weight;
            }
        }

        return matrix;
    }

    public double[,] GenerateMatrixFromXML(HttpPostedFile file)
    {
        XmlReader reader = XmlReader.Create(file.InputStream);
        XElement xmlfile = XElement.Load(reader);
        double[,] matrix = null;
        string xmlString = xmlfile.ToString();
        if (xmlString.Contains("xmlns=") == true)
        {
            xmlString = xmlString.Replace("xmlns=", "xmlns:xli=");
        }
        xmlfile = XElement.Parse(xmlString);

        try
        {
            //odszukanie grafu
            var query = from p in xmlfile.Elements("graph")
                        select p;

            XElement graph = query.First();
            int nodes = Int32.Parse(graph.Attribute("parse.nodes").Value);

            matrix = new double[nodes, nodes];

            for (int j = 0; j < nodes; j++)
            {
                for (int k = 0; k < nodes; k++)
                {
                    matrix[j, k] = INF;
                }
            }

            Hashtable nodeHashtable = new Hashtable();

            //odszukanie wszystkich wierzcholkow grafu
            query = from p in graph.Elements("node")
                    select p;

            int i = 0;
            foreach (XElement n in query)
            {
                nodeHashtable[n.Attribute("id").Value] = i;
                i++;
            }

            //odszukuje wszystkie krawedzie grafu
            query = from p in graph.Elements("edge")
                    select p;

            foreach (XElement e in query)
            {
                double weight = INF;
                try
                {
                    var query2 = from p in e.Elements("data")
                                 select p;

                    foreach (XElement data in query2)
                    {
                        if (data.Attribute("key").Value == "weight")
                        {
                            string value = data.Value.ToString();
                            value = value.Replace(".", ",");
                            weight = Double.Parse(value);
                        }
                    }
                }
                catch { }

                string source = e.Attribute("source").Value;
                string target = e.Attribute("target").Value;

                try
                {
                    if (e.Attribute("directed").Value == "true")
                    {
                        matrix[(int)nodeHashtable[source], (int)nodeHashtable[target]] = weight;
                    }
                }
                catch
                {
                    matrix[(int)nodeHashtable[source], (int)nodeHashtable[target]] = weight;
                    matrix[(int)nodeHashtable[target], (int)nodeHashtable[source]] = weight;
                }
            }
        }
        catch { }

        return matrix;
    }
}
