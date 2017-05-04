package kh.com.penhchet.repositories_nita;

import kh.com.penhchet.models_nita.Post;
import org.springframework.stereotype.Repository;

/**
 * Created by HP1 on 5/3/2017.
 */
@Repository
public interface PostRepository {

    public int save(Post post);
}
