async function get1(bno){ //비동기 함수를 만든것. async -> 비동기 처리 함수 명시.
    const result = await axios.get(`/replies/list/${bno}`) //await -> 비동기 호출
    // console.log(result) // read.html에 정의
    // console.log(result.data)
    return result.data; //프로미스를 넘긴다.
}

async function getList({bno, page, size, goLast}){
    // console.log(bno, page, size)
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})
    // console.log(result)
    console.log(result)
    if(goLast) {
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({bno:bno, page:lastPage, size:size})
    }
    return result.data
}

async function addReply(replyObj){
    const response = await axios.post(`/replies/`, replyObj)
    return response.data
}

async function getReply(rno){ //수정하는 녀석
    const response = await axios.get(`/replies/${rno}`)
    console.log(response)
    return response.data
}

async function modifyReply(replyObj){ //수정정보를 받아오는녀석
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno){
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}