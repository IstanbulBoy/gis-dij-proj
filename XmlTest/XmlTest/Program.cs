using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

using QuickGraph;
using QuickGraph.Serialization;
using QuickGraph.Serialization.DirectedGraphML;

namespace XmlTest
{
    class Program
    {
        static void Main(string[] args)
        {
            var g = new AdjacencyGraph<string, Edge<string>>();
            using (var xreader = XmlReader.Create("../../test.xml"))
            {
                g.DeserializeFromGraphML(xreader, id => id, (source, target, id) => new Edge<string>(source, target));
            }

            Console.WriteLine(g.EdgeCount);
            Console.WriteLine(g.VertexCount);

            //g.AddVertex("n11");
            //g.AddVertex("n12");

            //g.AddEdge(new Edge<string>("n0", "n11"));
            //g.AddEdge(new Edge<string>("n11", "n10"));


            using (var xwriter = XmlWriter.Create("../../testwrite.xml"))
                g.SerializeToGraphML(xwriter);

            Console.Read();
        }
    }
}
