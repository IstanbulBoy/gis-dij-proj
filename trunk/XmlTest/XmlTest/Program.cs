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
    class MyEdge : Edge<string>
    {
        [XmlAttribute("label")]
        public string label { get; set; }
        [XmlAttribute("color")]
        public string color { set; get; }
        [XmlAttribute("flag")]
        public string flag { set; get; }

        //public MyEdge(Node source, Node target)
        //    : base(source.id, target.id)
        //{

        //}

        public MyEdge(string source, string target)
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
            var g = new AdjacencyGraph<string, MyEdge>();

            using (var xreader = XmlReader.Create("../../test.xml"))
            {
                g.DeserializeFromGraphML(xreader, id => id, (source, target, id) => new MyEdge(source, target));
            }

            Console.WriteLine(g.EdgeCount);
            Console.WriteLine(g.VertexCount);

            //g.AddVertex("n11");
            //g.AddVertex("n12");

            //g.AddEdge(new Edge<string>("n0", "n11"));
            //g.AddEdge(new Edge<string>("n11", "n10"));

            using (var xwriter = XmlWriter.Create("../../testwrite.xml"))
                g.SerializeToGraphML(xwriter, AlgorithmExtensions.GetVertexIdentity(g), AlgorithmExtensions.GetEdgeIdentity(g));

            Console.Read();
        }
    }
}
