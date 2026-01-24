package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private static final String API_POSTS = "/api/posts";
  private static final String API_POSTS_WITH_ID = "/api/posts/\\d+";
  private static final String GET = "GET";
  private static final String POST = "POST";
  private static final String DELETE = "DELETE";

  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      if (GET.equals(method) && path.equals(API_POSTS)) {
        controller.all(resp);
        return;
      }
      if (GET.equals(method) && path.matches(API_POSTS_WITH_ID)) {
        final var id = extractId(path);
        controller.getById(id, resp);
        return;
      }
      if (POST.equals(method) && path.equals(API_POSTS)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (DELETE.equals(method) && path.matches(API_POSTS_WITH_ID)) {
        final var id = extractId(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof NotFoundException || e.getCause() instanceof NotFoundException) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
      } else {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
      }
    }
  }

  private long extractId (String path) {
    return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
  }
}

