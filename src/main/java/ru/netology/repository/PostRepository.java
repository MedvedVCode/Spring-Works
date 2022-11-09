package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotAllowedException;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private long initialMaxCount = 0;
    private AtomicLong maxPostCount = new AtomicLong(initialMaxCount);
    private ConcurrentHashMap<Long, Post> repository = new ConcurrentHashMap<>();

    public List<Post> all() {
        return repository.values()
                .stream()
                .filter(post -> !post.isRemoved())
                .toList();
    }

    public Optional<Post> getById(long id) {
        var post = repository.getOrDefault(id, null);
        if (post != null) {
            if (post.isRemoved()) post = null;
        }
        return Optional.ofNullable(post);
    }

    public Post save(Post post) {

        if (post.getId() < 0) {
            throw new NotAllowedException();
        } else if (post.getId() == 0) {
            post.setId(maxPostCount.incrementAndGet());
        }

        if (repository.putIfAbsent(post.getId(), post) == null) {
            if (maxPostCount.get() < post.getId()) {
                maxPostCount.set(post.getId());
            }
        } else if (repository.get(post.getId()).isRemoved()) {
            throw new NotAllowedException();
        } else {
            repository.put(post.getId(), post);
        }
        return post;
    }

    public void removeById(long id) {
        repository.get(id).setRemoved();
    }
}
