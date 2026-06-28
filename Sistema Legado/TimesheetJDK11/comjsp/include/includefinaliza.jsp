</BODY>
</HTML>
<jsp:useBean id="finaliza" class="combeans.finalizeBean" scope="page">
   <% finaliza.execute(iconexaobean, out, request, response, session); %>
</jsp:useBean>
