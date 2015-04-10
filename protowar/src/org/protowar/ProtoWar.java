package org.protowar;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProtoWar
 */
public class ProtoWar extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  private ProtoServerHandler _handler = new ProtoServerHandler();

  /**
  * Default Constructor.
  */
  public ProtoWar ()
  {
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet (HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doIt(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost (HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doIt(request, response);
  }

  private void doIt (HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    _handler.handle(response.getOutputStream(), request.getInputStream());
  }

}
