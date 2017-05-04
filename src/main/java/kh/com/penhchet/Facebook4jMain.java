package kh.com.penhchet;

import facebook4j.*;
import facebook4j.Like;
import facebook4j.Post;
import facebook4j.auth.AccessToken;
import kh.com.penhchet.repositories_nita.CommentRepository;
import kh.com.penhchet.repositories_nita.LikeRepository;
import kh.com.penhchet.repositories_nita.PostRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP1 on 5/3/2017.
 */
@SpringBootApplication
public class Facebook4jMain {
    public static void main(String args[]) throws FacebookException {
        ApplicationContext context = SpringApplication.run(Facebook4jMain.class, args);

        PostRepository postRepository = (PostRepository) context.getBean("postRepository");
        CommentRepository commentRepository = (CommentRepository)context.getBean("commentRepository");
        LikeRepository likeRepository = (LikeRepository) context.getBean("likeRepository");

        Facebook facebook = new FacebookFactory().getInstance();

        String appId = "776306919082812";
        String appSecret = "b2e74d95f3f04769aba7e94c9cc3096d";
        String accessToken = "EAALCDAomCzwBAPT2RAdw6qxkbSvv1P9pz2iYA98k6kGqP2MLtm3Kow0xkyCihymHHZAHRUZB3MMUkx0ez1mHJ9ZAKqZA6Lvou766XtfmIXUedY5vyIvO3EFNfQdBPBjuxUQmNYkbiFze0GYZCNZBMNZBCZCV8ahjp9eHD0mQZAC0MHoKZAKe1NNfY610UrtCLxJqgZD";
        facebook.setOAuthAppId(appId, appSecret);
        //facebook.setOAuthPermissions(commaSeparetedPermissions);
        AccessToken extendedToken = facebook.extendTokenExpiration(accessToken);
        facebook.setOAuthAccessToken(extendedToken);

        PagableList<Post> posts = facebook.getPosts("RHM.Production");
        Paging<Post> pagingPosts;
        do {
            for (Post post : posts) {
            if(!post.getType().equals("video")){
                break;
            }
                System.out.println("========> " + post);
                kh.com.penhchet.models_nita.Post postNita = new kh.com.penhchet.models_nita.Post();
                postNita.setPostId(post.getId());
                postNita.setName(post.getName());
                postNita.setPageId(1);
                postNita.setLink(post.getLink() + "");
                postNita.setMessage(post.getMessage());
                postNita.setObjectId(post.getObjectId());
                postNita.setCreatedTime(post.getCreatedTime());
                postNita.setUpdatedTime(post.getUpdatedTime());
                postNita.setStatusType(post.getStatusType());
                postNita.setType(post.getType());
                postNita.setShareCount(post.getSharesCount() == null ? 0 : post.getSharesCount());

                postRepository.save(postNita);

                List<Comment> fullComments = new ArrayList<>();
                try {
                    // get first few comments using getComments from post
                    PagableList<Comment> comments = post.getComments();
                    Paging<Comment> paging;
                    do {
                        for (Comment comment : comments) {
                            System.out.println("====> " + comment.getLikeCount());
                            fullComments.add(comment);
                            kh.com.penhchet.models_nita.Comment commentNita = new kh.com.penhchet.models_nita.Comment();
                            commentNita.setPostId(postNita.getPostId());
                            commentNita.setCommentCount(comment.getCommentCount() == null ? 0 : comment.getCommentCount());
                            commentNita.setUserId(comment.getFrom().getId());
                            commentNita.setUsername(comment.getFrom().getName());
                            commentNita.setCommentId(comment.getId());
                            commentNita.setMessage(comment.getMessage());
                            commentNita.setLikeCount(comment.getLikeCount());
                            commentNita.setCreatedTime(comment.getCreatedTime());

                            commentRepository.save(commentNita);
                        }

                        // get next page
                        // NOTE: somehow few comments will not be included.
                        // however, this won't affect much on our research
                        paging = comments.getPaging();
                    } while ((paging != null) &&
                            ((comments = facebook.fetchNext(paging)) != null));

                } catch (FacebookException ex) {
                    ex.printStackTrace();
                }

                List<Like> fullLikes = new ArrayList<>();
                try {
                    // get first few likes using getLikes from post
                    PagableList<Like> likes = post.getLikes();
                    Paging<Like> paging;
                    do {
                        for (Like like: likes) {
                            System.out.println("LIKE ==> " + like);
                            kh.com.penhchet.models_nita.Like likeNita = new kh.com.penhchet.models_nita.Like();
                            likeNita.setPostId(post.getId());
                            likeNita.setUserId(like.getId());
                            likeNita.setLikeId(like.getId());
                            likeNita.setName(like.getName());
                            likeRepository.save(likeNita);
                            fullLikes.add(like);
                        }

                        // get next page
                        // NOTE: somehow few likes will not be included.
                        // however, this won't affect much on our research
                        paging = likes.getPaging();
                    } while ((paging != null) &&
                            ((likes = facebook.fetchNext(paging)) != null));

                } catch (FacebookException ex) {
                    ex.printStackTrace();
                }
            }
            pagingPosts = posts.getPaging();
        }while ((pagingPosts != null) &&
                ((posts = facebook.fetchNext(pagingPosts)) != null));
    }

}

