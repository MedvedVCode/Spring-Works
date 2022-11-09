package ru.netology.repository;

import org.springframework.stereotype.Repository;
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
        return repository.values().stream().toList();
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(repository.getOrDefault(id, null));
    }

    public Post save(Post post) {
        if (post.getId() <= 0) {
            post.setId(maxPostCount.incrementAndGet());
        }
        if (repository.put(post.getId(), post) == null) {
            if (maxPostCount.get() < post.getId()) {
                maxPostCount.set(post.getId());
            }
        }
        return post;
    }

    public void removeById(long id) {
        repository.remove(id);
    }
}
