<%@ Page Language="C#" AutoEventWireup="true"  CodeFile="Default.aspx.cs" Inherits="_Default" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>GiS</title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
        <h2>0. Info</h2>
    </div>
    <hr />
    <div>
        <h2>1. Wybór grafu</h2>
        <asp:RadioButton ID="RadioButton1" runat="server" GroupName="GraphMaker" 
            Text="<b>Wczytywanie grafu z pliku</b>" 
            oncheckedchanged="RadioButton1_CheckedChanged" AutoPostBack="True" Checked="True" />
        
        <asp:Panel ID="pnlLoad" runat="server">        
            <asp:FileUpload ID="FileUpload1" runat="server" />
            <br />
            <asp:Button ID="btnLoad" runat="server" Text="Wczytaj z pliku" />
        </asp:Panel>
        
        <br />
        
        <asp:RadioButton ID="RadioButton2" runat="server" GroupName="GraphMaker" 
            Text="<b>Generowanie grafu</b>" oncheckedchanged="RadioButton2_CheckedChanged" 
            AutoPostBack="True" />
            
        <asp:Panel ID="pnlGenerate" runat="server" Enabled="false">
            Rodzaj: <asp:DropDownList ID="ddlGraphType" runat="server" AutoPostBack="True" 
                onselectedindexchanged="ddlGraphType_SelectedIndexChanged">
                <asp:ListItem Value="complete">pełny</asp:ListItem>
                <asp:ListItem Value="notcomplete">niepełny</asp:ListItem>
            </asp:DropDownList>
            <br />
            Liczba wierzchołków 
            <asp:TextBox ID="tbNodeCount" runat="server"></asp:TextBox>
            <br />
            Przedział możliwych wag krawędzi: 
            min:
            <asp:TextBox ID="tbMinWeight" runat="server"></asp:TextBox>
            max: 
            <asp:TextBox ID="tbMaxWeight" runat="server"></asp:TextBox>
            <br />
            <asp:Panel ID="pnlProb" runat="server" Visible="false">
                Prawdopodobieństwo wystąpienia krawędzi:
                <asp:TextBox ID="tbEdgeProb" runat="server"></asp:TextBox>%
            </asp:Panel>
            <asp:Button ID="btnGenerate" runat="server" Text="Generuj" onclick="btnGenerate_Click" />
        </asp:Panel>
    </div>
    <hr />
    <div>
        <h2>2. Macierz sąsiedztwa</h2>
        <asp:Panel ID="pnlMatrix" runat="server">
        </asp:Panel>
    </div>
    <hr />
    <div>
        <h2>3. Wybór algorytmu</h2>
        <asp:RadioButtonList ID="RadioButtonList1" runat="server">
            <asp:ListItem Value="0" Selected="True">Przegląd zupełny</asp:ListItem>
            <asp:ListItem Value="1">Branch & Bound</asp:ListItem>
            <asp:ListItem Value="2">Algorytm zachłanny</asp:ListItem>
        </asp:RadioButtonList>
        Określ początkowy wierzchołek
        <asp:TextBox ID="tbStartNode" runat="server"></asp:TextBox>
        <br />
        <asp:Button ID="btnStart" runat="server" Text="Uruchom algorytm" 
            onclick="btnStart_Click" />
    </div>
    <hr />
    <div>
        <h2>4. Wyniki</h2>
        <asp:Panel ID="pnlResults" runat="server">
        </asp:Panel>
        <br />
        <asp:Button ID="btnGenerateXML" runat="server" Text="Generuj wynikowy plik XML" Visible="False" />
    </div>
    </form> 
</body>
</html>
