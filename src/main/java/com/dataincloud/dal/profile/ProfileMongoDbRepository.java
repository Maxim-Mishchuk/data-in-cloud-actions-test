package com.dataincloud.dal.profile;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileMongoDbRepository extends MongoRepository<ProfileDocument, Long> {
}
