public class Post implements Comparable<Post>{
    private String poster; // the user who posted the post
    private String ID;
    private String content;
    private int likes = 0;

    private HashTable<String, String> seenBy; // Hash table to store all users who have seen the post
    private HashTable<String, String> likedBy;// Hash table to store all users who have liked the post

    public Post(String poster, String ID, String content) {
        this.poster = poster;
        this.ID = ID;
        this.content = content;
        this.likes = 0;
        this.seenBy = new HashTable();
        this.likedBy = new HashTable();
    }

    public Post(){}

    public int getLikes(){
        return likes;
    }


    public String getID(){
        return ID;
    }

    public String getContent(){
        return content;
    }

    /**
     * @return The author of the post (User object)
     */
    public String getPoster(){
        return poster;
    }

    /**
     * Makes the post seen by the user with the provided username
     */
    public void makeSeenBy(String userID){
        seenBy.put(userID, userID);
    }

    /**
     * @return True if the user has seen the post, false otherwise
     */
    public boolean isSeenBy(String userID){
        return seenBy.containsKey(userID);
    }

    /**
     * @return True if the user has liked the post, false otherwise
     */
    public boolean isLikedBy(String userName) {
        return likedBy.containsKey(userName);
    }

    /**
     * Makes the user with the given username unlike the post.
     */
    public void unlikeBy(String userName) {
        if (likedBy.containsKey(userName)) {
            likedBy.remove(userName);
            likes--;
        }
    }

    /**
     * Makes the user with the given username like the post.
     */
    public void likeBy(String userName) {
        if (!likedBy.containsKey(userName)) {
            likedBy.put(userName, userName);
            likes++;
        }
    }

    public int compareTo(Post post) {
        if (this.likes > post.likes)
            return 1;
        else if (this.likes < post.likes)
            return -1;
        else
            return this.ID.compareTo(post.ID);
    }
}
