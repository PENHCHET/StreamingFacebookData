package kh.com.penhchet.repositories_nita;

import kh.com.penhchet.models_nita.Like;
import org.springframework.stereotype.Repository;

/**
 * Created by HP1 on 5/4/2017.
 */
@Repository
public interface LikeRepository {

    public int save(Like like);
}
