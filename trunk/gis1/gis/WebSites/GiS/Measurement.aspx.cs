using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class Measurement : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }

    protected void btnGenerate_Click(object sender, EventArgs e)
    {
        if (Page.IsValid)
        {
            GraphGenerator graphGenerator = new GraphGenerator();
            double[,] matrix;
            List<double[,]> matrixList = new List<double[,]>();
            DateTime ExecutionStartTime;
            DateTime ExecutionStopTime;
            TimeSpan ExecutionTime;

            for (int i = 0; i < Int32.Parse(tbRepeat.Text) * 2; i++)
            {
                matrix = graphGenerator.GenerateGraph(Int32.Parse(tbEdgeProb.Text), Int32.Parse(tbNodeCount.Text), Int32.Parse(tbMinWeight.Text), Int32.Parse(tbMaxWeight.Text));
                matrixList.Add(matrix);

                ExecutionStartTime = DateTime.Now;
                do
                {
                    ExecutionStopTime = DateTime.Now;
                    ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);
                } while (ExecutionTime.TotalMilliseconds < 50);
            }

            Session["matrixList"] = matrixList;

            lblGener.Text = "Wygenerowano.";
        }
    }

    protected void btnStart_Click(object sender, EventArgs e)
    {
        if (Session["matrixList"] != null)
        {
            List<double[,]> matrixList = (List<double[,]>)Session["matrixList"];
            string algType = RadioButtonList1.SelectedValue.ToString();
            TspAlgorithm tspAlgorithm = new TspAlgorithm();

            List<object> results = new List<object>();

            if (algType == "0")
            {
                results = tspAlgorithm.GetPermutationMeasurement(matrixList, 0, Int32.Parse(tbRepeat.Text));
            }
            else if (algType == "1")
            {
                results = tspAlgorithm.GetBBMeasurement(matrixList, 0, Int32.Parse(tbRepeat.Text));
            }
            else if (algType == "2")
            {
                results = tspAlgorithm.GreedyMeasurement(matrixList, 0, Int32.Parse(tbRepeat.Text));
            }

            string htmlCode = "średni czas wykonywania: " + results[1] + " milisec.<br />" +
                              "średnia długość ścieżki: " + results[0];
            
            LiteralControl control = new LiteralControl(htmlCode);
            pnlResults.Controls.Add(control);

            lblGener.Text = "";
        }
    }
}
