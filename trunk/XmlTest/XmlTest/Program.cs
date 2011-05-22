using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

using QuickGraph;
using QuickGraph.Serialization;
using QuickGraph.Serialization.DirectedGraphML;
using QuickGraph.Algorithms;

namespace XmlTest
{
    class MyAdjacencyGraph : AdjacencyGraph<Node, MyEdge>
    {
        [XmlAttribute("name")]
        public string name { get; set; }
        [XmlAttribute("size")]
        public int size { set; get; }
    }

    class MyEdge : Edge<Node>
    {
        [XmlAttribute("label")]
        public string label { get; set; }
        [XmlAttribute("color")]
        public string color { set; get; }
        [XmlAttribute("flag")]
        public int flag { set; get; }
        [XmlAttribute("weight")]
        public double weight { set; get; }

        public MyEdge(Node source, Node target)
            : base(source, target)
        {

        }
    }
    
    class Node
    {
        [XmlAttribute("id")]
        public string id { set; get; }
        [XmlAttribute("label")]
        public string label { set; get; }
        [XmlAttribute("color")]
        public string color { set; get; }
        [XmlAttribute("2D")]
        public string _2D { set; get; }
        [XmlAttribute("3D")]
        public string _3D { set; get; }
        [XmlAttribute("nD")]
        public string nD { set; get; }

        public Node(string id)
        {
        }

        public Node()
        {
        }
    }

    //class 

    class Program
    {
        static void Main(string[] args)
        {
            var g = new MyAdjacencyGraph();

            using (var xreader = XmlReader.Create("../../test.xml"))
            {
                g.DeserializeFromGraphML(xreader, id => new Node(id), (source, target, id) => new MyEdge(source, target));
            }

            Console.WriteLine(g.EdgeCount);
            Console.WriteLine(g.VertexCount);

            using (var xwriter = XmlWriter.Create("../../testwrite.xml"))
                g.SerializeToGraphML(xwriter, AlgorithmExtensions.GetVertexIdentity(g), AlgorithmExtensions.GetEdgeIdentity(g));

            Console.Read();
        }
    }
}
