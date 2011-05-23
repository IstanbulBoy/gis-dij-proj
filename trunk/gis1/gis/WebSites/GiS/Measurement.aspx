<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Measurement.aspx.cs" Inherits="Measurement" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
        <h2>0. Info</h2>
        <hr />
        <asp:HyperLink ID="HyperLink1" runat="server" NavigateUrl="Default.aspx">Przejdź do strony głównej</asp:HyperLink>
        <hr />
        <h2>1. Generowanie grafów</h2>
        Liczba wierzchołków
        <asp:TextBox ID="tbNodeCount" runat="server"></asp:TextBox>
        <asp:RequiredFieldValidator ID="RequiredFieldValidator4" runat="server" 
            ErrorMessage="Pole wymagane." ControlToValidate="tbNodeCount" 
            Display="Dynamic" ValidationGroup="generatorValid"></asp:RequiredFieldValidator>
        <asp:RegularExpressionValidator ID="RegularExpressionValidator4" 
            runat="server" ErrorMessage="Tylko liczby" ControlToValidate="tbNodeCount" 
            ValidationGroup="generatorValid" ValidationExpression="^\d+$" 
            Display="Dynamic"></asp:RegularExpressionValidator>
        <br />
        Przedział możliwych wag krawędzi: 
        min:
        <asp:TextBox ID="tbMinWeight" runat="server"></asp:TextBox>
        <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" 
            ErrorMessage="Pole wymagane." ControlToValidate="tbMinWeight" 
            Display="Dynamic" ValidationGroup="generatorValid"></asp:RequiredFieldValidator>
        <asp:RegularExpressionValidator ID="RegularExpressionValidator1" 
            runat="server" ErrorMessage="Tylko liczby" ControlToValidate="tbMinWeight" 
            ValidationGroup="generatorValid" ValidationExpression="^\d+$" 
            Display="Dynamic"></asp:RegularExpressionValidator>
        max: 
        <asp:TextBox ID="tbMaxWeight" runat="server"></asp:TextBox>
        <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" 
            ErrorMessage="Pole wymagane." ControlToValidate="tbMaxWeight" 
            Display="Dynamic" ValidationGroup="generatorValid"></asp:RequiredFieldValidator>
        <asp:RegularExpressionValidator ID="RegularExpressionValidator2" 
            runat="server" ErrorMessage="Tylko liczby" ControlToValidate="tbMaxWeight" 
            ValidationGroup="generatorValid" ValidationExpression="^\d+$" 
            Display="Dynamic"></asp:RegularExpressionValidator>
        <br />
        Prawdopodobieństwo wystąpienia krawędzi:
        <asp:TextBox ID="tbEdgeProb" runat="server"></asp:TextBox>%
        <asp:RequiredFieldValidator ID="RequiredFieldValidator3" runat="server" 
            ErrorMessage="Pole wymagane." ControlToValidate="tbEdgeProb" 
            Display="Dynamic" ValidationGroup="generatorValid"></asp:RequiredFieldValidator>
        <asp:RegularExpressionValidator ID="RegularExpressionValidator3" 
            runat="server" ErrorMessage="Tylko liczby" ControlToValidate="tbEdgeProb" 
            ValidationGroup="generatorValid" ValidationExpression="^\d+$" 
            Display="Dynamic"></asp:RegularExpressionValidator>
        <br />
        Liczba powtórzeń algorytmu (liczba grafów): 
        <asp:TextBox ID="tbRepeat" runat="server"></asp:TextBox>
        <asp:RequiredFieldValidator ID="RequiredFieldValidator5" runat="server" 
            ErrorMessage="Pole wymagane." ControlToValidate="tbRepeat" 
            Display="Dynamic" ValidationGroup="generatorValid"></asp:RequiredFieldValidator>
        <asp:RegularExpressionValidator ID="RegularExpressionValidator5" 
            runat="server" ErrorMessage="Tylko liczby" ControlToValidate="tbRepeat" 
            ValidationGroup="generatorValid" ValidationExpression="^\d+$" 
            Display="Dynamic"></asp:RegularExpressionValidator>
        <br />
        <asp:Button ID="btnGenerate" runat="server" Text="Generuj zbiór grafów" 
            onclick="btnGenerate_Click" ValidationGroup="generatorValid" />
        <br />
        <asp:Label ID="lblGener" runat="server" ForeColor="Red"></asp:Label>
        <hr />
        <h2>2. Wybór algorytmu</h2>
        <asp:RadioButtonList ID="RadioButtonList1" runat="server">
            <asp:ListItem Value="0" Selected="True">Przegląd zupełny</asp:ListItem>
            <asp:ListItem Value="1">Branch & Bound</asp:ListItem>
            <asp:ListItem Value="2">Algorytm zachłanny</asp:ListItem>
        </asp:RadioButtonList>
        <asp:Button ID="btnStart" runat="server" Text="Uruchom algorytm" 
            onclick="btnStart_Click" />
        <hr />
        <h2>3. Wyniki</h2>
        <asp:Panel ID="pnlResults" runat="server">
        </asp:Panel>    
    </div>
    </form>
</body>
</html>
