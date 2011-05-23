using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.Xml.Serialization;
using QuickGraph;

/// <summary>
/// Summary description for TSPEdge
/// </summary>
public class TSPEdge : Edge<TSPNode>
{
    [XmlAttribute("label")]
    public string label { get; set; }
    [XmlAttribute("color")]
    public string color { set; get; }
    [XmlAttribute("flag")]
    public int flag { set; get; }
    [XmlAttribute("weight")]
    public double weight { set; get; }

    public TSPEdge(TSPNode source, TSPNode target)
        : base(source, target)
    {

    }
}