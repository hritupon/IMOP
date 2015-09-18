package com.opinion.models.enums;

/**
 * Created with IntelliJ IDEA.
 * User: ramachandra.as
 * Date: 23/11/14
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PartitionType {
    partition0, partition1, partition2, partition3, partition4, partition5;

    /*
     *
     * Do the hardcoding of zones here itself and return
     * appropriate parition on the following method
     */
    public static PartitionType getPartitionType(String key){
        return PartitionType.valueOf("partition".concat(getPartitionKey(key)));
    }

    /*
     * DJB2 hashing.
     */
    public static String getPartitionKey(String key){
        long hash = 5381;
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 5) + hash) + key.charAt(i);
        }
        return String.valueOf(((int) Math.abs(hash % getModuloNeedle())));
    }

    public static int getModuloNeedle(){
        return PartitionType.values().length - 1;
    }

    public static PartitionType getTopicPartition(){
        return partition5;
    }

    public static PartitionType getTopicDetailsPartition(){
        return partition4;
    }

    public static PartitionType getUserPartition(){
        return partition0;
    }
    public static PartitionType getPostsPartition(){
        return partition1;
    }
}
