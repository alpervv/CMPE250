import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Main class to manage instagram app
 * @author Alper Vural, Student ID:2023400066
 * @since 11/24/2024
 */
public class Main {
    private static HashTable<String, User> users = new HashTable<>(); // A hash table to store all users, maps usernames to User objects
    private static HashTable<String, Post> posts = new HashTable<>(); // A hash table to store all posts, maps post IDs to Post objects

    /**
     * Reads the input file, performs backend operations for account creation, post creation, feed generation, liking/unliking and scrolling through feed.
     * Additionally, generate a log file.
     * @param args
     */
    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();

        File inputFile = new File("src/type2_small.txt");
        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
                while (br.ready()) {
                    String line = br.readLine();
                    String[] tokens = line.split(" ");
                    // Check which command is given in the input file
                    switch (tokens[0]) {
                        case "create_user" -> {
                            String outputStr = createUser(tokens[1]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();
                        }
                        case "follow_user" -> {
                            String outputStr = followUser(tokens[1], tokens[2]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();
                        }
                        case "unfollow_user" -> {
                            String outputStr = unfollowUser(tokens[1], tokens[2]);
                            bw.write(outputStr);
                            bw.newLine();
                            // bw.flush();
                        }
                        case "create_post" -> {
                            String outputStr = createPost(tokens[1], tokens[2], tokens[3]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();
                        }
                        case "see_post" -> {
                            String outputStr = seePost(tokens[1], tokens[2]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();

                        }
                        case "see_all_posts_from_user" -> {
                            String outputStr = seeAllPosts(tokens[1], tokens[2]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();
                        }
                        case "toggle_like" -> {
                            String outputStr = toggleLike(tokens[1], tokens[2]);
                            bw.write(outputStr);
                            bw.newLine();
                            //bw.flush();
                        }
                        case "generate_feed" -> {
                            ArrayList<String> outputStr = generateFeed(tokens[1], Integer.parseInt(tokens[2]));
                            for (String s : outputStr) {
                                bw.write(s);
                                bw.newLine();
                                //bw.flush();
                            }
                        }
                        case "scroll_through_feed" -> {
                            String[] slice = getSliceOfArray(tokens, 3, tokens.length); // Stores liking status of posts in the feed.

                            ArrayList<String> outputStr = scroll(tokens[1], Integer.parseInt(tokens[2]), slice);
                            for (String s : outputStr) {
                                bw.write(s);
                                bw.newLine();
                                //bw.flush();
                            }
                        }
                        case "sort_posts" -> {
                            ArrayList<String> outputStr = sortPosts(tokens[1]);
                            for (String s : outputStr) {
                                bw.write(s);
                                bw.newLine();
                                //bw.flush();
                            }
                        }
                    }

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
        System.out.println(System.currentTimeMillis() - currentTime);
    }


    /**
     * Creates a new user with the given userName, and puts in the hash table of all users.
     * @param userName the username of the new user
     * @return Logs for user creation
     */
    private static String createUser(String userName) {
        if (users.containsKey(userName)) { // Log error if userName already exists
            return "Some error occurred in create_user.";
        }
        users.put(userName, new User(userName));
        return "Created user with Id "+userName+".";
    }

    /**
     * Makes user1 follow user2
     * @return Logs for the follow operation
     */
    private static String followUser(String user1, String user2) {
        if (!users.containsKey(user1) || !users.containsKey(user2)) { // Log error if one of the users don't exist
            return "Some error occurred in follow_user.";
        }
        if (user1.equals(user2)) { // Log error if the user tries to follow himself.
            return "Some error occurred in follow_user.";
        }

        if (users.get(user1).followUser(users.get(user2))) // Check if the user1 already follows user2, follow if not.
            return user1+" followed "+user2+".";
        return "Some error occurred in follow_user.";
    }

    /**
     * Makes user1 unfollow user2
     * @return Logs for the ufollow operation
     */
    private static String unfollowUser(String user1, String user2) {
        if (!users.containsKey(user1) || !users.containsKey(user2)) { // Log error if one of the users don't exist
            return "Some error occurred in unfollow_user.";
        }
        if (user1.equals(user2)) { // Log error if the user tries to unfollow himself.
            return "Some error occurred in unfollow_user.";
        }
        if (users.get(user1).unfollowUser(users.get(user2))) // Check if the user1 already unfollows user2, unfollow if not.
            return user1+" unfollowed "+user2+".";
        return "Some error occurred in unfollow_user.";
    }

    /**
     * Creates a post with the given ID and content. This is done by calling the post method on the user.
     * Puts the post in the hash table of all posts.
     * @param userId The username of the post's author
     * @param postId ID of the post that is being created
     * @param content content inside the post
     * @return Logs for post creation
     */
    private static String createPost(String userId, String postId, String content) {
        if (posts.containsKey(postId) || !users.containsKey(userId)) { // Log error if post with the same ID exists.
            return "Some error occurred in create_post.";
        }
        Post newPost = new Post(userId, postId, content);
        posts.put(postId, newPost);
        users.get(userId).post(newPost);
        return userId+" created a post with Id "+postId+".";
    }

    /**
     * Makes the user with the given username see the post with the given ID.
     * @param userId username for the user
     * @param postId ID of the post
     * @return Logs for the user seeing the post
     */
    private static String seePost(String userId, String postId) {
        if (!posts.containsKey(postId) || !users.containsKey(userId)) { // Log error if the user of post doesn't exist
            return "Some error occurred in see_post.";
        }
        Post post = posts.get(postId);
        post.makeSeenBy(userId);
        return userId+" saw "+postId+".";
        // Implement feed part later for this function (is it necessary??????????)
    }

    /**
     * Makes one user view all posts of another user.
     * @param viewerId The username for the user who is viewing the posts
     * @param viewedId The username for the user whose posts are being viewed
     * @return Logs for see all posts operation
     */
    private static String seeAllPosts(String viewerId, String viewedId) {
        if (!users.containsKey(viewerId) || !users.containsKey(viewedId)) { // Log error message if one of the users doesn't exist
            return "Some error occurred in see_all_posts_from_user.";
        }
        ArrayList<Post> viewedPosts = users.get(viewedId).getPosts(); // Get all posts as a list
        for (Post post : viewedPosts) {
            post.makeSeenBy(viewerId);
        }
        return viewerId+" saw all posts of "+viewedId+".";
    }

    /**
     * Makes the user like the post if they didn't like it, makes the user unlike the post if they liked it beforehand.
     * This done by updating the likedBy field in the corresponding post.
     * @param userId username of the user who is toggling like
     * @param postId ID of the post the operation is done
     * @return
     */
    private static String toggleLike(String userId, String postId) {
        if (!posts.containsKey(postId) || !users.containsKey(userId)) {
            return "Some error occurred in toggle_like.";
        }
        Post post = posts.get(postId);
        if (post.isLikedBy(userId)) {
            post.unlikeBy(userId);
            return userId+" unliked "+postId+".";
        }
        else {
            post.likeBy(userId);
            post.makeSeenBy(userId);
            return userId+" liked "+postId+".";
        }
    }

    /**
     * Generates a feed for the user containing at most num posts. Feed properties are explained in a separate file.
     * @param userId username for the user whose feed will be generated
     * @param num maximum number of posts to be created in the feed.
     * @return Logs for feed generation
     */
    private static ArrayList<String> generateFeed(String userId, int num) {
        ArrayList<String> toReturn = new ArrayList<>(num+1); // Log strings as an arraylist

        if(!users.containsKey(userId)) { // If the user doesn't exist, log an error message
            toReturn.add("Some error occurred in generate_feed.");
            return toReturn;
        }

        toReturn.add("Feed for "+userId+":");

        User currentUser = users.get(userId);

        // Gather all posts from users followed by current user
        ArrayList<Post> allRelevantPosts = new ArrayList<>();
        for(User followed: currentUser.getFollowedUsers()){
            for (Post post : followed.getPosts()) {
                if (!post.isSeenBy(userId) )
                    allRelevantPosts.add(post);
            }
        }

        allRelevantPosts = BinaryHeap.heapSort(allRelevantPosts); // Sort all the posts according to their likes and lexicographical order

        currentUser.clearFeed(); // Clear the feed of the user to allow a new feed generation

        // Add num number of posts to the feed.
        for (int i = 0; i < num; i++) {
            if (allRelevantPosts.isEmpty()){ // Check if there are available posts to avoid a NullPointerException
                toReturn.add("No more posts available for "+userId+".");
                break;
            }
            Post currentPost = allRelevantPosts.removeLast();
            currentUser.addTofeed(currentPost);
            toReturn.add("Post ID: "+currentPost.getID()+", Author: "+currentPost.getPoster()+", Likes: "+currentPost.getLikes()); //
        }

        return toReturn;
    }


    /**
     * Makes num number of posts seen by the given user, from the highest liked post to least liked post.
     * Generates the most up-to-date feed for the user before doing this.
     * If there aren't enough posts in the user's feed, performs the operation on all available posts and indicates this in the logs
     * @param userId The username of the user who will be seeing the posts in his feed
     * @param num The number of posts the user will see.
     * @param likeArray An array containing num Strings each of which are either "1" or "0". 0 represents the user didn't like the post and 1 represents he liked it.
     * @return Logs for scroll operation
     */
    private static ArrayList<String> scroll(String userId, int num, String[] likeArray) {
        ArrayList<String> toReturn = new ArrayList<>();

        if (!users.containsKey(userId)) { // If the user doesn't exit, log an error message
            toReturn.add("Some error occurred in scroll_through_feed.");
            return toReturn;
        }

        toReturn.add(userId+" is scrolling through feed:");

        generateFeed(userId, num); // Generate the most up-to-date feed

        int likeIndex = 0;

        // Exit if there are no posts in the feed.
        if (users.get(userId).getFeed().size() == 0){
            toReturn.add("No more posts in feed.");
            return toReturn;
        }

        // See posts until the end of the feed
        for (Post post : users.get(userId).getFeed()) {
            post.makeSeenBy(userId);
            if (likeArray[likeIndex].equals("1")) {
                post.likeBy(userId);
                toReturn.add(userId + " saw " + post.getID() + " while scrolling and clicked the like button.");
            }
            else {
                toReturn.add(userId + " saw " + post.getID() + " while scrolling.");
            }
            likeIndex++;
        }

        if (likeIndex < num){
            toReturn.add("No more posts in feed.");
        }
        return toReturn;
    }

    /**
     * Sorts all posts which are posted by a user from most liked to least liked.
     * @param userID username of the user
     * @return Logs for sorting operation
     */
    private static ArrayList<String> sortPosts(String userID) {
        ArrayList<String> toReturn = new ArrayList<>();

        if (!users.containsKey(userID)) {
            toReturn.add("Some error occurred in sort_posts.");
            return toReturn;
        }

        toReturn.add("Sorting "+userID+"'s posts:");

        ArrayList<Post> allPosts = users.get(userID).getPosts();
        allPosts = BinaryHeap.heapSort(allPosts); // Posts are sorted from least liked to most liked

        for (int i = allPosts.size()-1; i >= 0; i--) {
            Post post = allPosts.get(i);
            toReturn.add(post.getID()+", Likes: "+post.getLikes());
        }

        return toReturn;
    }

    /**
     * Copies the elements of  arr from index start to the index end (end is exclusive) to another array and returns the new array.
     */
    public static<T> T[] getSliceOfArray(T[] arr, int start, int end) {

        // Get the slice of the Array
        T[] slice = (T[]) Array.newInstance(arr.getClass().getComponentType(), end - start);

        // Copy elements of arr to slice
        for (int i = 0; i < slice.length; i++) {
            slice[i] = arr[start + i];
        }

        // return the slice
        return slice;
    }
}