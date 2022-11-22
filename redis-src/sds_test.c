#include "sds_test.h"
#include <stdio.h>
#include <malloc.h>
#include <string.h>

struct sdshdr8* alloc_sdshdr8(unsigned char len, unsigned char alloc, unsigned char flags) {
    struct sdshdr8* ret = calloc(sizeof(struct sdshdr8) + len, 1);
    if (ret)
        memcpy(ret, &(struct sdshdr8 const){ .len = len, .alloc = alloc, .flags = flags},
                    sizeof(struct sdshdr8));
    return ret;
}

int main() {
    printf("%ld\n", sizeof(struct sdshdr8));
    printf("%ld\n", sizeof(struct array));
    printf("%ld\n", sizeof(struct empty_array));

    char str[] = "arvin";
    struct sdshdr8* sds_str = alloc_sdshdr8(sizeof(str), 2, 1);
    strcpy(sds_str->buf, str);

    unsigned char flags = sds_str->buf[-1];
    printf("flags: %d\n", flags);

    printf("ptr len : %lu\n", sizeof(void *));

    //如果有指向buf的（void*）指针,可以通过下面方法在调试时看结果体的值
    //(struct sdshdr8 *)(sds_str->buf-3)
}