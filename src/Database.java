interface Database {
    boolean write(String text);
    String find(String key, String value);
    boolean delete(String key, String value);
    String getFilename();
    void setFilename(String filename);
}
