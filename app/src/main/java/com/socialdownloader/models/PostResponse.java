package com.socialdownloader.models;

import com.google.gson.annotations.SerializedName;

public class PostResponse {
    @SerializedName("graphql")
    Graphql graphql;

    public PostResponse() {
    }

    public PostResponse(Graphql graphql) {
        this.graphql = graphql;
    }

    public Graphql getGraphql() {
        return graphql;
    }

    public void setGraphql(Graphql graphql) {
        this.graphql = graphql;
    }
}
