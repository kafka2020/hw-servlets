package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {

  ConcurrentHashMap<Long, Post> map;
  AtomicLong nextId;

  public PostRepository() {
    this.map = new ConcurrentHashMap<>();
    this.nextId = new AtomicLong(0);
  }

  public List<Post> all() {
    return new ArrayList<>(map.values());
  }

  public Optional<Post> getById(long id) {
    Post post = map.get(id);
    return Optional.ofNullable(post);
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      long id = nextId.incrementAndGet();
      Post newPost = new Post(id, post.getContent());
      map.put(id, newPost);
      return newPost;
    } else {
      if (map.containsKey(post.getId())) {
        long id = post.getId();
        Post updated = new Post(id, post.getContent());
        map.put(id, updated);
        return updated;
      } else {
        throw new NotFoundException();
      }
    }
  }

  public void removeById(long id) {
    Post removed = map.remove(id);
    if (removed == null) {
      throw new NotFoundException();
    }
  }
}
