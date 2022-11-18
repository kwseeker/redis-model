#ifndef REDIS_SRC_TEST_H
#define REDIS_SRC_TEST_H

struct __attribute__ ((__packed__)) sdshdr8 {
    unsigned char len; /* used */
    unsigned char alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};

struct __attribute__ ((__packed__)) array {
    //char buf[];
    char *buf;
};

struct __attribute__ ((__packed__)) empty_array {
    //char buf[];
    char buf[0];
};

#endif //REDIS_SRC_TEST_H
