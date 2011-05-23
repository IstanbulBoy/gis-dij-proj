using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class _Default : System.Web.UI.Page 
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }

    protected void btnGenerate_Click(object sender, EventArgs e)
    {
        GraphGenerator graphGenerator = new GraphGenerator();
        int[,] matrix;

        string graphType = ddlGraphType.SelectedValue.ToString();

        if (graphType == "notcomplete")
        {
            matrix = graphGenerator.GenerateGraph(Int32.Parse(tbEdgeProb.Text), Int32.Parse(tbNodeCount.Text), Int32.Parse(tbMinWeight.Text), Int32.Parse(tbMaxWeight.Text));
        }
        else
        {
            matrix = graphGenerator.GenerateCompleteGraph(Int32.Parse(tbNodeCount.Text), Int32.Parse(tbMinWeight.Text), Int32.Parse(tbMaxWeight.Text));
        }

        Table tbl = graphGenerator.PrintMatrix(matrix);
        pnlMatrix.Controls.Add(tbl);
        Session["matrix"] = matrix;
    }

    protected void ddlGraphType_SelectedIndexChanged(object sender, EventArgs e)
    {
        string graphType = ddlGraphType.SelectedValue.ToString();

        if (graphType == "notcomplete")
        {
            pnlProb.Visible = true;
        }
        else
        {
            pnlProb.Visible = false;
        }
    }

    protected void RadioButton1_CheckedChanged(object sender, EventArgs e)
    {
        pnlLoad.Enabled = true;
        pnlGenerate.Enabled = false;
    }

    protected void RadioButton2_CheckedChanged(object sender, EventArgs e)
    {
        pnlLoad.Enabled = false;
        pnlGenerate.Enabled = true;
    }

    protected void btnStart_Click(object sender, EventArgs e)
    {
        if (Session["matrix"] != null)
        {
            int[,] matrix = (int[,])Session["matrix"];
            string algType = RadioButtonList1.SelectedValue.ToString();
            GraphGenerator graphGenerator = new GraphGenerator();
            TspAlgorithm tspAlgorithm = new TspAlgorithm();
            Random random = new Random();

            int startNode = random.Next() % matrix.GetLength(1);
            if (String.IsNullOrEmpty(tbStartNode.Text) == false)
            {
                startNode = Int32.Parse(tbStartNode.Text) - 1;
            }

            Table tbl = graphGenerator.PrintMatrix(matrix);
            pnlMatrix.Controls.Add(tbl);
            List<object> results = new List<object>();

            if (algType == "0")
            {
                results = tspAlgorithm.GetPermutationAlg(matrix, startNode);
            }
            else if (algType == "1")
            {
                //
            }
            else if (algType == "2")
            {
                results = tspAlgorithm.GreedyAlg(matrix, startNode);
            }

            string htmlCode = "";

            if (results[3].ToString() == "noresult")
            {
                htmlCode = "czas wykonywania: " + results[2] + "<br />" +
                            "wierzcholek startowy: " + (startNode + 1).ToString() + "<br />" +
                            "długość ścieżki: brak rozwiazania <br />" +
                            "kolejność wierzchołków: brak rozwiazania ";
            }
            else
            {
                htmlCode = "czas wykonywania: " + results[2] + "<br />" + 
                            "wierzcholek startowy: " + (startNode+1).ToString() + "<br />" +
                            "długość ścieżki: " + results[0] + "<br />" +
                            "kolejność wierzchołków: " + results[1];
            }
            LiteralControl control = new LiteralControl(htmlCode);
            pnlResults.Controls.Add(control);

            btnGenerateXML.Visible = true;
        }
    }
}
